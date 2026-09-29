package com.ridelink.driver.service;

import com.ridelink.driver.dto.CreateVehicleRequest;
import com.ridelink.driver.dto.UpdateVehicleRequest;
import com.ridelink.driver.dto.VehicleResponse;
import com.ridelink.driver.exception.DuplicateResourceException;
import com.ridelink.driver.exception.ResourceNotFoundException;
import com.ridelink.driver.model.Vehicle;
import com.ridelink.driver.model.VehicleStatus;
import com.ridelink.driver.repository.DriverProfileRepository;
import com.ridelink.driver.repository.VehicleRepository;
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
 * Unit Tests for VehicleService.
 * Primary Owner: Nanayakkara S.N.M (IT24102468)
 */
@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private DriverProfileRepository driverProfileRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle sampleVehicle;

    @BeforeEach
    void setUp() {
        sampleVehicle = new Vehicle("drv-001", "WP-CAD-1234", "Toyota", "Prius", "SEDAN", "White", 4, VehicleStatus.ACTIVE);
        sampleVehicle.setId("veh-100");
        sampleVehicle.setCreatedAt(LocalDateTime.now());
        sampleVehicle.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should register vehicle when driver exists and registration number is unique")
    void testRegisterVehicle_Success() {
        CreateVehicleRequest request = new CreateVehicleRequest(
                "drv-001", "WP-CAD-1234", "Toyota", "Prius", "SEDAN", "White", 4
        );

        when(driverProfileRepository.existsById("drv-001")).thenReturn(true);
        when(vehicleRepository.existsByRegistrationNumber("WP-CAD-1234")).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(sampleVehicle);

        VehicleResponse response = vehicleService.registerVehicle(request);

        assertNotNull(response);
        assertEquals("veh-100", response.getId());
        assertEquals("WP-CAD-1234", response.getRegistrationNumber());
        assertEquals("Prius", response.getModel());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when registering vehicle for non-existent driver")
    void testRegisterVehicle_DriverNotFound() {
        CreateVehicleRequest request = new CreateVehicleRequest(
                "drv-nonexistent", "WP-CAD-1234", "Toyota", "Prius", "SEDAN", "White", 4
        );

        when(driverProfileRepository.existsById("drv-nonexistent")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> vehicleService.registerVehicle(request));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when registration number already exists")
    void testRegisterVehicle_DuplicateRegistration() {
        CreateVehicleRequest request = new CreateVehicleRequest(
                "drv-001", "WP-CAD-1234", "Toyota", "Prius", "SEDAN", "White", 4
        );

        when(driverProfileRepository.existsById("drv-001")).thenReturn(true);
        when(vehicleRepository.existsByRegistrationNumber("WP-CAD-1234")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> vehicleService.registerVehicle(request));
    }

    @Test
    @DisplayName("Should retrieve vehicle by ID")
    void testGetVehicleById_Success() {
        when(vehicleRepository.findById("veh-100")).thenReturn(Optional.of(sampleVehicle));

        VehicleResponse response = vehicleService.getVehicleById("veh-100");

        assertNotNull(response);
        assertEquals("veh-100", response.getId());
        assertEquals("Toyota", response.getMake());
    }

    @Test
    @DisplayName("Should update vehicle details")
    void testUpdateVehicle_Success() {
        UpdateVehicleRequest request = new UpdateVehicleRequest("Toyota", "Aqua", "HATCHBACK", "Silver", 4, VehicleStatus.ACTIVE);
        when(vehicleRepository.findById("veh-100")).thenReturn(Optional.of(sampleVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(sampleVehicle);

        VehicleResponse response = vehicleService.updateVehicle("veh-100", request);

        assertNotNull(response);
        assertEquals("Aqua", sampleVehicle.getModel());
        assertEquals("Silver", sampleVehicle.getColour());
    }

    @Test
    @DisplayName("Should get all vehicles for a driver")
    void testGetVehiclesByDriverId() {
        when(driverProfileRepository.existsById("drv-001")).thenReturn(true);
        when(vehicleRepository.findByDriverId("drv-001")).thenReturn(List.of(sampleVehicle));

        List<VehicleResponse> list = vehicleService.getVehiclesByDriverId("drv-001");

        assertEquals(1, list.size());
        assertEquals("WP-CAD-1234", list.get(0).getRegistrationNumber());
    }
}
