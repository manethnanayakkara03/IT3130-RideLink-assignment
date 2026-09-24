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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for RideService.
 * Primary Owner: Munasinghe D.D.T (IT24102566)
 */
@ExtendWith(MockitoExtension.class)
class RideServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private DriverServiceClient driverServiceClient;

    @Mock
    private FareServiceClient fareServiceClient;

    @InjectMocks
    private RideService rideService;

    private Ride sampleRide;
    private DriverDto sampleDriver;

    @BeforeEach
    void setUp() {
        sampleRide = new Ride(100L, "Colombo Fort", 6.9319, 79.8478, "Dehiwala", 6.8511, 79.8653, 500.0);
        sampleRide.setId(1L);

        sampleDriver = new DriverDto();
        sampleDriver.setId(50L);
        sampleDriver.setAccountId(20L);
        sampleDriver.setLicenseNumber("B7654321");
        sampleDriver.setAvailabilityStatus("AVAILABLE");
    }

    @Test
    @DisplayName("Should create ride request with fare estimate")
    void testCreateRide_Success() {
        CreateRideRequest request = new CreateRideRequest(
                100L, "Colombo Fort", 6.9319, 79.8478, "Dehiwala", 6.8511, 79.8653
        );

        FareResponseDto fareDto = new FareResponseDto();
        fareDto.setTotalFare(750.0);
        fareDto.setIsEstimate(true);

        when(fareServiceClient.getFareEstimate(anyDouble(), anyDouble())).thenReturn(fareDto);
        when(rideRepository.save(any(Ride.class))).thenReturn(sampleRide);

        RideResponse response = rideService.createRide(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(RideStatus.REQUESTED, response.getStatus());
        verify(rideRepository, times(1)).save(any(Ride.class));
    }

    @Test
    @DisplayName("Should assign available driver to ride")
    void testAssignDriver_Success() {
        when(rideRepository.findById(1L)).thenReturn(Optional.of(sampleRide));
        when(driverServiceClient.getAvailableDrivers(null)).thenReturn(List.of(sampleDriver));
        when(rideRepository.save(any(Ride.class))).thenReturn(sampleRide);

        RideResponse response = rideService.assignDriver(1L);

        assertNotNull(response);
        assertEquals(RideStatus.ASSIGNED, sampleRide.getStatus());
        assertEquals(50L, sampleRide.getDriverId());
        assertNotNull(sampleRide.getAssignedAt());
        verify(driverServiceClient, times(1)).updateDriverAvailability(50L, "BUSY");
    }

    @Test
    @DisplayName("Negative Scenario 1: Should throw NoDriverAvailableException when no drivers are available")
    void testAssignDriver_NoDriverAvailable() {
        when(rideRepository.findById(1L)).thenReturn(Optional.of(sampleRide));
        when(driverServiceClient.getAvailableDrivers(null)).thenReturn(Collections.emptyList());

        assertThrows(NoDriverAvailableException.class, () -> rideService.assignDriver(1L));
        assertEquals(RideStatus.REQUESTED, sampleRide.getStatus());
    }

    @Test
    @DisplayName("Should transition from ASSIGNED to ACCEPTED on driver acceptance")
    void testAcceptRide_Success() {
        sampleRide.setStatus(RideStatus.ASSIGNED);
        sampleRide.setDriverId(50L);

        when(rideRepository.findById(1L)).thenReturn(Optional.of(sampleRide));
        when(rideRepository.save(any(Ride.class))).thenReturn(sampleRide);

        RideResponse response = rideService.acceptRide(1L);

        assertNotNull(response);
        assertEquals(RideStatus.ACCEPTED, sampleRide.getStatus());
        assertNotNull(sampleRide.getAcceptedAt());
    }

    @Test
    @DisplayName("Negative Scenario 2: Should reject starting ride before acceptance (REQUESTED -> IN_PROGRESS)")
    void testStartRide_InvalidStateTransition() {
        sampleRide.setStatus(RideStatus.REQUESTED);

        when(rideRepository.findById(1L)).thenReturn(Optional.of(sampleRide));

        assertThrows(InvalidRideStateException.class, () -> rideService.startRide(1L));
    }

    @Test
    @DisplayName("Should transition from ACCEPTED to IN_PROGRESS on ride start")
    void testStartRide_Success() {
        sampleRide.setStatus(RideStatus.ACCEPTED);
        sampleRide.setDriverId(50L);

        when(rideRepository.findById(1L)).thenReturn(Optional.of(sampleRide));
        when(rideRepository.save(any(Ride.class))).thenReturn(sampleRide);

        RideResponse response = rideService.startRide(1L);

        assertNotNull(response);
        assertEquals(RideStatus.IN_PROGRESS, sampleRide.getStatus());
        assertNotNull(sampleRide.getStartedAt());
    }

    @Test
    @DisplayName("Should complete ride, calculate final fare, and free driver")
    void testCompleteRide_Success() {
        sampleRide.setStatus(RideStatus.IN_PROGRESS);
        sampleRide.setDriverId(50L);
        sampleRide.setStartedAt(LocalDateTime.now().minusMinutes(20));

        FareResponseDto finalFareDto = new FareResponseDto();
        finalFareDto.setTotalFare(850.0);
        finalFareDto.setIsEstimate(false);

        when(rideRepository.findById(1L)).thenReturn(Optional.of(sampleRide));
        when(fareServiceClient.calculateFinalFare(eq(1L), anyDouble(), anyDouble())).thenReturn(finalFareDto);
        when(rideRepository.save(any(Ride.class))).thenReturn(sampleRide);

        RideResponse response = rideService.completeRide(1L);

        assertNotNull(response);
        assertEquals(RideStatus.COMPLETED, sampleRide.getStatus());
        assertEquals(850.0, sampleRide.getFinalFare());
        assertNotNull(sampleRide.getCompletedAt());
        verify(driverServiceClient, times(1)).updateDriverAvailability(50L, "AVAILABLE");
    }

    @Test
    @DisplayName("Negative Scenario: Should reject completing ride when not IN_PROGRESS")
    void testCompleteRide_InvalidState() {
        sampleRide.setStatus(RideStatus.REQUESTED);
        when(rideRepository.findById(1L)).thenReturn(Optional.of(sampleRide));

        assertThrows(InvalidRideStateException.class, () -> rideService.completeRide(1L));
    }

    @Test
    @DisplayName("Should cancel ride and record cancellation reason")
    void testCancelRide_Success() {
        sampleRide.setStatus(RideStatus.REQUESTED);
        when(rideRepository.findById(1L)).thenReturn(Optional.of(sampleRide));
        when(rideRepository.save(any(Ride.class))).thenReturn(sampleRide);

        CancelRideRequest cancelRequest = new CancelRideRequest("Passenger changed plans");
        RideResponse response = rideService.cancelRide(1L, cancelRequest);

        assertNotNull(response);
        assertEquals(RideStatus.CANCELLED, sampleRide.getStatus());
        assertEquals("Passenger changed plans", sampleRide.getCancellationReason());
    }
}
