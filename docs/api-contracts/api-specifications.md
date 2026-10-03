# RideLink REST API Specifications & Contracts

**Document Code**: IT3130-API-01  
**Project**: RideLink – Backend Microservices for a Ride-Sharing Platform  

---

## 1. Account Service (Port :8081)
Owner: Silva L.T.R.D (IT24102723) | Database: `ridelink_account_db`

| Method | Endpoint | Description | Auth Required | Status Codes |
|---|---|---|---|---|
| `POST` | `/api/auth/register/passenger` | Register a new passenger account | None | 201, 400, 409 |
| `POST` | `/api/auth/register/driver` | Register a new driver account | None | 201, 400, 409 |
| `POST` | `/api/auth/login` | Authenticate user & issue signed JWT | None | 200, 401 |
| `GET` | `/api/accounts/{id}` | Retrieve profile of account | Bearer JWT | 200, 401, 404 |
| `PUT` | `/api/accounts/{id}` | Update profile details | Bearer JWT | 200, 400, 401, 404 |
| `PATCH` | `/api/accounts/{id}/status` | Update account status (ACTIVE/SUSPENDED) | ROLE_ADMIN | 200, 403, 404 |
| `GET` | `/api/accounts/{id}/exists` | Interservice verification for Driver Service | None | 200 |

---

## 2. Driver & Vehicle Service (Port :8082)
Owner: Nanayakkara S.N.M (IT24102468) | Database: `ridelink_driver_db`

| Method | Endpoint | Description | Auth Required | Status Codes |
|---|---|---|---|---|
| `POST` | `/api/drivers` | Create driver profile (verifies Account Service) | Optional | 201, 400, 404, 409 |
| `GET` | `/api/drivers/{id}` | Get driver operational profile | Optional | 200, 404 |
| `PUT` | `/api/drivers/{id}` | Update license and operational service area | Optional | 200, 404, 409 |
| `PATCH` | `/api/drivers/{id}/availability` | Update status (AVAILABLE, UNAVAILABLE, BUSY) | Optional | 200, 404 |
| `PATCH` | `/api/drivers/{id}/location` | Update simulated GPS location coordinates | Optional | 200, 404 |
| `GET` | `/api/drivers/available` | Interservice: Query available eligible drivers | None | 200 |
| `POST` | `/api/vehicles` | Register vehicle for driver | Optional | 201, 400, 404, 409 |
| `GET` | `/api/vehicles/{id}` | Get vehicle details | Optional | 200, 404 |
| `PUT` | `/api/vehicles/{id}` | Update vehicle information | Optional | 200, 404 |
| `GET` | `/api/drivers/{driverId}/vehicles` | List all vehicles registered by driver | Optional | 200, 404 |

---

## 3. Ride Management Service (Port :8083)
Owner: Munasinghe D.D.T (IT24102566) | Database: `ridelink_ride_db`

| Method | Endpoint | Description | Status Codes |
|---|---|---|---|
| `POST` | `/api/rides` | Request ride (calls Fare Service for estimate) | 201, 400 |
| `GET` | `/api/rides/{id}` | Get ride details and current lifecycle status | 200, 404 |
| `POST` | `/api/rides/{id}/assign` | Assign available driver (calls Driver Service) | 200, 400, 404 |
| `POST` | `/api/rides/{id}/accept` | Driver accepts assigned ride | 200, 400, 404 |
| `POST` | `/api/rides/{id}/start` | Driver starts trip (status -> IN_PROGRESS) | 200, 400, 404 |
| `POST` | `/api/rides/{id}/complete` | Driver completes trip (calls Fare Service for final fare) | 200, 400, 404 |
| `POST` | `/api/rides/{id}/cancel` | Passenger/Driver cancels ride with reason | 200, 400, 404 |
| `GET` | `/api/rides/passenger/{passengerId}` | List ride history for passenger | 200 |
| `GET` | `/api/rides/driver/{driverId}` | List ride history for driver | 200 |

---

## 4. Fare & Payment Service (Port :8084)
Owner: Priyamalka W.D.N (IT24102758) | Database: `ridelink_payment_db`

| Method | Endpoint | Description | Status Codes |
|---|---|---|---|
| `POST` | `/api/fares/estimate` | Estimate ride fare based on distance & time | 200, 400 |
| `POST` | `/api/fares/final` | Calculate final fare for completed trip | 200, 400 |
| `POST` | `/api/payments` | Simulate payment (supports simulateFailure=true) | 201, 400 |
| `GET` | `/api/payments/{id}` | Get payment transaction by ID | 200, 404 |
| `GET` | `/api/payments/ride/{rideId}` | Get payment transaction for ride | 200, 404 |
| `GET` | `/api/receipts/{paymentId}` | Get official payment receipt by payment ID | 200, 404 |
| `GET` | `/api/receipts/ride/{rideId}` | Get official payment receipt by ride ID | 200, 404 |
