# RideLink End-to-End Workflow & Sequence Diagrams

**Document Code**: IT3130-SEQ-01  
**Project**: RideLink – Backend Microservices for a Ride-Sharing Platform  

---

## 1. Happy-Path End-to-End Sequence Diagram (18 Steps)

This sequence diagram illustrates the complete end-to-end lifecycle across the 4 microservices:

```mermaid
sequenceDiagram
    autonumber
    actor Passenger as Passenger (Client)
    actor Driver as Driver (Client)
    participant AS as Account Service (:8081)
    participant DS as Driver Service (:8082)
    participant RS as Ride Service (:8083)
    participant FS as Fare & Payment Service (:8084)

    %% Step 1-4: Registration & Login
    Note over Passenger, AS: Phase 1: Identity & Authentication
    Passenger->>AS: POST /api/auth/register/passenger
    AS-->>Passenger: 201 Created (User ID, Role: PASSENGER)
    Driver->>AS: POST /api/auth/register/driver
    AS-->>Driver: 201 Created (User ID, Role: DRIVER)
    Passenger->>AS: POST /api/auth/login
    AS-->>Passenger: 200 OK (JWT Token)
    Driver->>AS: POST /api/auth/login
    AS-->>Driver: 200 OK (JWT Token)

    %% Step 5-8: Driver Preparation
    Note over Driver, DS: Phase 2: Driver & Vehicle Onboarding
    Driver->>DS: POST /api/drivers (accountId, license, area)
    DS->>AS: GET /api/accounts/{id}/exists (Interservice Verification)
    AS-->>DS: 200 OK (exists: true, role: DRIVER)
    DS-->>Driver: 201 Created (DriverProfile ID, AVAILABLE)
    Driver->>DS: POST /api/vehicles (registration, type, capacity)
    DS-->>Driver: 201 Created (Vehicle ID, ACTIVE)
    Driver->>DS: PATCH /api/drivers/{id}/availability (AVAILABLE)
    DS-->>Driver: 200 OK (Status: AVAILABLE)
    Driver->>DS: PATCH /api/drivers/{id}/location (lat, lon, area)
    DS-->>Driver: 200 OK (Simulated Location Recorded)

    %% Step 9-12: Fare Estimation, Ride Request & Assignment
    Note over Passenger, FS: Phase 3: Ride Booking & Interservice Assignment
    Passenger->>FS: POST /api/fares/estimate (distance, minutes)
    FS-->>Passenger: 200 OK (Estimated Fare: LKR 830.00)
    Passenger->>RS: POST /api/rides (pickup, destination, coordinates)
    RS->>FS: POST /api/fares/estimate (Interservice Call)
    FS-->>RS: 200 OK (Fare Estimate)
    RS-->>Passenger: 201 Created (Ride ID, Status: REQUESTED)
    Passenger->>RS: POST /api/rides/{id}/assign
    RS->>DS: GET /api/drivers/available (Interservice Call)
    DS-->>RS: 200 OK (List of Available Drivers)
    RS->>DS: PATCH /api/drivers/{driverId}/availability (status: BUSY)
    DS-->>RS: 200 OK (Driver marked BUSY)
    RS-->>Passenger: 200 OK (Ride Status: ASSIGNED, Driver ID: 1)

    %% Step 13-15: Ride Lifecycle Transitions
    Note over Driver, RS: Phase 4: Ride Execution State Machine
    Driver->>RS: POST /api/rides/{id}/accept
    RS-->>Driver: 200 OK (Ride Status: ACCEPTED)
    Driver->>RS: POST /api/rides/{id}/start
    RS-->>Driver: 200 OK (Ride Status: IN_PROGRESS)
    Driver->>RS: POST /api/rides/{id}/complete
    RS->>FS: POST /api/fares/final (rideId, actualDistance, duration)
    FS-->>RS: 200 OK (Final Fare Calculated: LKR 830.00)
    RS->>DS: PATCH /api/drivers/{driverId}/availability (status: AVAILABLE)
    DS-->>RS: 200 OK (Driver marked AVAILABLE)
    RS-->>Driver: 200 OK (Ride Status: COMPLETED, Final Fare: LKR 830.00)

    %% Step 16-18: Payment & Receipt
    Note over Passenger, FS: Phase 5: Payment Settlement & Receipt
    Passenger->>FS: POST /api/payments (rideId, amount, method, simulateFailure=false)
    FS-->>Passenger: 201 Created (Payment Status: SUCCESS, Ref: TXN-XXXX)
    Passenger->>FS: GET /api/receipts/ride/{rideId}
    FS-->>Passenger: 200 OK (Receipt: REC-YYYYMMDD-XXXX, Breakdown, Paid)
```

---

## 2. Negative Scenario 1: Driver Unavailable During Assignment

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant RS as Ride Management Service (:8083)
    participant DS as Driver & Vehicle Service (:8082)

    Client->>RS: POST /api/rides/1/assign
    RS->>DS: GET /api/drivers/available
    DS-->>RS: 200 OK ([]) (Empty driver list)
    Note over RS: No available drivers found! Throws NoDriverAvailableException
    RS-->>Client: 404 Not Found {"status": 404, "error": "Not Found", "message": "No available drivers found in the system for assignment"}
```

---

## 3. Negative Scenario 2: Invalid Ride Status Lifecycle Transition

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant RS as Ride Management Service (:8083)

    Note over RS: Ride #1 is in REQUESTED status
    Client->>RS: POST /api/rides/1/start
    Note over RS: Illegal transition! Must be in ACCEPTED state. Throws InvalidRideStateException
    RS-->>Client: 400 Bad Request {"status": 400, "error": "Bad Request", "message": "Cannot start ride in state: REQUESTED. Ride must be in ACCEPTED state."}
```

---

## 4. Negative Scenario 3: Simulated Payment Processing Failure

```mermaid
sequenceDiagram
    autonumber
    actor Passenger
    participant FS as Fare & Payment Service (:8084)

    Passenger->>FS: POST /api/payments (rideId: 1, amount: 830.0, simulateFailure=true)
    Note over FS: Simulated card issuer declines transaction
    FS-->>Passenger: 201 Created {"status": "FAILED", "simulatedFailureReason": "Simulated card payment authorization declined by issuer"}
```
