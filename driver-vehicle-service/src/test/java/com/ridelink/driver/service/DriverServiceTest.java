package com.ridelink.driver.service;

import com.ridelink.driver.client.AccountServiceClient;
import com.ridelink.driver.dto.*;
import com.ridelink.driver.exception.DuplicateResourceException;
import com.ridelink.driver.exception.InvalidDriverStateException;
import com.ridelink.driver.exception.ResourceNotFoundException;
import com.ridelink.driver.model.AvailabilityStatus;
import com.ridelink.driver.model.DriverProfile;
import com.ridelink.driver.repository.DriverProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for DriverService.
 * Primary Owner: Nanayakkara S.N.M (IT24102468)
 */
@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverProfileRepository driverProfileRepository;

    @Mock
    private AccountServiceClient accountServiceClient;

    @InjectMocks
    private DriverService driverService;

    private DriverProfile sampleDriver;

    @BeforeEach
    void setUp() {
        sampleDriver = new DriverProfile("acc-driver-010", "B1234567", AvailabilityStatus.AVAILABLE, "Colombo", 6.9271, 79.8612);
        sampleDriver.setId("drv-001");
        sampleDriver.setCreatedAt(LocalDateTime.now());
        sampleDriver.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should successfully create a driver profile when account is verified")
    void testCreateDriverProfile_Success() {
        CreateDriverProfileRequest request = new CreateDriverProfileRequest(
                "acc-driver-010", "B1234567", "Colombo", 6.9271, 79.8612
        );

        when(driverProfileRepository.existsByAccountId("acc-driver-010")).thenReturn(false);
        when(driverProfileRepository.existsByLicenseNumber("B1234567")).thenReturn(false);
        when(accountServiceClient.verifyAccount("acc-driver-010"))
                .thenReturn(new AccountVerificationDto(true, "acc-driver-010", "DRIVER", "ACTIVE"));
        when(driverProfileRepository.save(any(DriverProfile.class))).thenReturn(sampleDriver);

        DriverProfileResponse response = driverService.createDriverProfile(request);

        assertNotNull(response);
        assertEquals("drv-001", response.getId());
        assertEquals("acc-driver-010", response.getAccountId());
        assertEquals("B1234567", response.getLicenseNumber());
        assertEquals(AvailabilityStatus.AVAILABLE, response.getAvailabilityStatus());
        verify(driverProfileRepository, times(1)).save(any(DriverProfile.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when Account Service indicates account does not exist")
    void testCreateDriverProfile_AccountNotFound() {
        CreateDriverProfileRequest request = new CreateDriverProfileRequest(
                "acc-nonexistent", "B9999999", "Colombo", 6.9271, 79.8612
        );

        when(driverProfileRepository.existsByAccountId("acc-nonexistent")).thenReturn(false);
        when(driverProfileRepository.existsByLicenseNumber("B9999999")).thenReturn(false);
        when(accountServiceClient.verifyAccount("acc-nonexistent"))
                .thenReturn(new AccountVerificationDto(false, null, null, null));

        assertThrows(ResourceNotFoundException.class, () -> driverService.createDriverProfile(request));
    }

    @Test
    @DisplayName("Should throw InvalidDriverStateException when Account has role other than DRIVER")
    void testCreateDriverProfile_NotADriver() {
        CreateDriverProfileRequest request = new CreateDriverProfileRequest(
                "acc-passenger-001", "B1234567", "Colombo", 6.9271, 79.8612
        );

        when(driverProfileRepository.existsByAccountId("acc-passenger-001")).thenReturn(false);
        when(driverProfileRepository.existsByLicenseNumber("B1234567")).thenReturn(false);
        when(accountServiceClient.verifyAccount("acc-passenger-001"))
                .thenReturn(new AccountVerificationDto(true, "acc-passenger-001", "PASSENGER", "ACTIVE"));

        assertThrows(InvalidDriverStateException.class, () -> driverService.createDriverProfile(request));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when profile already exists for account")
    void testCreateDriverProfile_DuplicateAccount() {
        CreateDriverProfileRequest request = new CreateDriverProfileRequest(
                "acc-driver-010", "B1234567", "Colombo", 6.9271, 79.8612
        );

        when(driverProfileRepository.existsByAccountId("acc-driver-010")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> driverService.createDriverProfile(request));
    }

    @Test
    @DisplayName("Should update driver availability successfully")
    void testUpdateAvailability_Success() {
        when(driverProfileRepository.findById("drv-001")).thenReturn(Optional.of(sampleDriver));
        when(driverProfileRepository.save(any(DriverProfile.class))).thenReturn(sampleDriver);

        DriverProfileResponse response = driverService.updateAvailability("drv-001", AvailabilityStatus.BUSY);

        assertNotNull(response);
        assertEquals(AvailabilityStatus.BUSY, sampleDriver.getAvailabilityStatus());
    }

    @Test
    @DisplayName("Should update simulated location successfully")
    void testUpdateLocation_Success() {
        UpdateLocationRequest request = new UpdateLocationRequest(6.9319, 79.8478, "Fort");
        when(driverProfileRepository.findById("drv-001")).thenReturn(Optional.of(sampleDriver));
        when(driverProfileRepository.save(any(DriverProfile.class))).thenReturn(sampleDriver);

        DriverProfileResponse response = driverService.updateLocation("drv-001", request);

        assertNotNull(response);
        assertEquals(6.9319, sampleDriver.getLatitude());
        assertEquals("Fort", sampleDriver.getServiceArea());
    }

    @Test
    @DisplayName("Should return available drivers")
    void testGetAvailableDrivers() {
        when(driverProfileRepository.findByAvailabilityStatus(AvailabilityStatus.AVAILABLE))
                .thenReturn(List.of(sampleDriver));

        List<DriverProfileResponse> available = driverService.getAvailableDrivers(null);

        assertEquals(1, available.size());
        assertEquals("B1234567", available.get(0).getLicenseNumber());
    }
}
