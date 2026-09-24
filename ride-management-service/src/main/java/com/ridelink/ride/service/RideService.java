package com.ridelink.ride.service;

import com.ridelink.ride.client.DriverServiceClient;
import com.ridelink.ride.client.FareServiceClient;
import com.ridelink.ride.dto.*;
import com.ridelink.ride.exception.InvalidRideStateException;
import com.ridelink.ride.exception.NoDriverAvailableException;
import com.ridelink.ride.exception.ResourceNotFoundException;
import com.ridelink.ride.model.Ride;
import com.ridelink.ride.model.RideStatus;
import com.ridelink.ride.repository.RideRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RideService {

    private final RideRepository rideRepository;
    private final DriverServiceClient driverServiceClient;
    private final FareServiceClient fareServiceClient;

    public RideService(RideRepository rideRepository,
                       DriverServiceClient driverServiceClient,
                       FareServiceClient fareServiceClient) {
        this.rideRepository = rideRepository;
        this.driverServiceClient = driverServiceClient;
        this.fareServiceClient = fareServiceClient;
    }

    public RideResponse createRide(CreateRideRequest request) {
        double distanceKm = calculateDistanceKm(
                request.getPickupLatitude(), request.getPickupLongitude(),
                request.getDestinationLatitude(), request.getDestinationLongitude()
        );
        double estimatedMinutes = Math.max(5.0, distanceKm * 2.5); // simulated speed

        // Inter-service call to Fare Service for estimate
        FareResponseDto fareEstimate = fareServiceClient.getFareEstimate(distanceKm, estimatedMinutes);
        Double estimatedFare = fareEstimate != null ? fareEstimate.getTotalFare() : null;

        Ride ride = new Ride(
                request.getPassengerId(),
                request.getPickupLocation(),
                request.getPickupLatitude(),
                request.getPickupLongitude(),
                request.getDestination(),
                request.getDestinationLatitude(),
                request.getDestinationLongitude(),
                estimatedFare
        );

        Ride saved = rideRepository.save(ride);
        return new RideResponse(saved);
    }

    public RideResponse assignDriver(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with ID: " + rideId));

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new InvalidRideStateException("Cannot assign driver to ride in state: " + ride.getStatus());
        }

        // Inter-service call to Driver Service to find available drivers
        List<DriverDto> availableDrivers = driverServiceClient.getAvailableDrivers(null);
        if (availableDrivers == null || availableDrivers.isEmpty()) {
            throw new NoDriverAvailableException("No available drivers found in the system for assignment");
        }

        DriverDto assignedDriver = availableDrivers.get(0);
        ride.setDriverId(assignedDriver.getId());
        ride.setStatus(RideStatus.ASSIGNED);
        ride.setAssignedAt(LocalDateTime.now());

        // Update driver availability to BUSY
        driverServiceClient.updateDriverAvailability(assignedDriver.getId(), "BUSY");

        Ride updated = rideRepository.save(ride);
        return new RideResponse(updated);
    }

    public RideResponse acceptRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with ID: " + rideId));

        if (ride.getStatus() != RideStatus.ASSIGNED) {
            throw new InvalidRideStateException("Cannot accept ride in state: " + ride.getStatus() + ". Ride must be in ASSIGNED state.");
        }

        ride.setStatus(RideStatus.ACCEPTED);
        ride.setAcceptedAt(LocalDateTime.now());

        Ride updated = rideRepository.save(ride);
        return new RideResponse(updated);
    }

    public RideResponse startRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with ID: " + rideId));

        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new InvalidRideStateException("Cannot start ride in state: " + ride.getStatus() + ". Ride must be in ACCEPTED state.");
        }

        ride.setStatus(RideStatus.IN_PROGRESS);
        ride.setStartedAt(LocalDateTime.now());

        Ride updated = rideRepository.save(ride);
        return new RideResponse(updated);
    }

    public RideResponse completeRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with ID: " + rideId));

        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            throw new InvalidRideStateException("Cannot complete ride in state: " + ride.getStatus() + ". Ride must be in IN_PROGRESS state.");
        }

        LocalDateTime completedAt = LocalDateTime.now();
        ride.setCompletedAt(completedAt);
        ride.setStatus(RideStatus.COMPLETED);

        // Calculate actual distance & duration
        double distanceKm = calculateDistanceKm(
                ride.getPickupLatitude(), ride.getPickupLongitude(),
                ride.getDestinationLatitude(), ride.getDestinationLongitude()
        );

        double durationMinutes = 15.0; // default duration
        if (ride.getStartedAt() != null) {
            long minutes = Duration.between(ride.getStartedAt(), completedAt).toMinutes();
            durationMinutes = Math.max(1.0, (double) minutes);
        }

        // Inter-service call to Fare Service for final fare calculation
        FareResponseDto finalFareDto = fareServiceClient.calculateFinalFare(rideId, distanceKm, durationMinutes);
        if (finalFareDto != null && finalFareDto.getTotalFare() != null) {
            ride.setFinalFare(finalFareDto.getTotalFare());
        } else if (ride.getEstimatedFare() != null) {
            ride.setFinalFare(ride.getEstimatedFare());
        }

        // Free driver availability
        if (ride.getDriverId() != null) {
            driverServiceClient.updateDriverAvailability(ride.getDriverId(), "AVAILABLE");
        }

        Ride updated = rideRepository.save(ride);
        return new RideResponse(updated);
    }

    public RideResponse cancelRide(Long rideId, CancelRideRequest request) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with ID: " + rideId));

        // Cancellation is allowed only from REQUESTED, ASSIGNED, or ACCEPTED
        if (ride.getStatus() == RideStatus.COMPLETED || ride.getStatus() == RideStatus.CANCELLED || ride.getStatus() == RideStatus.IN_PROGRESS) {
            throw new InvalidRideStateException("Cannot cancel ride in state: " + ride.getStatus());
        }

        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancelledAt(LocalDateTime.now());
        ride.setCancellationReason(request.getReason());

        // Free driver if one was assigned
        if (ride.getDriverId() != null) {
            driverServiceClient.updateDriverAvailability(ride.getDriverId(), "AVAILABLE");
        }

        Ride updated = rideRepository.save(ride);
        return new RideResponse(updated);
    }

    @Transactional(readOnly = true)
    public RideResponse getRideById(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with ID: " + id));
        return new RideResponse(ride);
    }

    @Transactional(readOnly = true)
    public List<RideResponse> getRidesByPassengerId(Long passengerId) {
        return rideRepository.findByPassengerIdOrderByRequestedAtDesc(passengerId).stream()
                .map(RideResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RideResponse> getRidesByDriverId(Long driverId) {
        return rideRepository.findByDriverIdOrderByRequestedAtDesc(driverId).stream()
                .map(RideResponse::new)
                .collect(Collectors.toList());
    }

    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;
        return Math.round(Math.max(1.0, distance) * 100.0) / 100.0;
    }
}
