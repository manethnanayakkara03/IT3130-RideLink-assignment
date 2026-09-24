package com.ridelink.driver.service;

import com.ridelink.driver.dto.CreateVehicleRequest;
import com.ridelink.driver.dto.UpdateVehicleRequest;
import com.ridelink.driver.dto.VehicleResponse;
import com.ridelink.driver.exception.DuplicateResourceException;
import com.ridelink.driver.exception.ResourceNotFoundException;
import com.ridelink.driver.model.Vehicle;
import com.ridelink.driver.repository.DriverProfileRepository;
import com.ridelink.driver.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DriverProfileRepository driverProfileRepository;

    public VehicleService(VehicleRepository vehicleRepository,
                          DriverProfileRepository driverProfileRepository) {
        this.vehicleRepository = vehicleRepository;
        this.driverProfileRepository = driverProfileRepository;
    }

    public VehicleResponse registerVehicle(CreateVehicleRequest request) {
        if (!driverProfileRepository.existsById(request.getDriverId())) {
            throw new ResourceNotFoundException("Driver profile not found with ID: " + request.getDriverId());
        }

        if (vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new DuplicateResourceException("Vehicle with registration number " + request.getRegistrationNumber() + " already exists");
        }

        Vehicle vehicle = new Vehicle(
                request.getDriverId(),
                request.getRegistrationNumber(),
                request.getMake(),
                request.getModel(),
                request.getVehicleType(),
                request.getColour(),
                request.getSeatCapacity(),
                request.getStatus()
        );

        Vehicle saved = vehicleRepository.save(vehicle);
        return new VehicleResponse(saved);
    }

    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + id));
        return new VehicleResponse(vehicle);
    }

    public VehicleResponse updateVehicle(Long id, UpdateVehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + id));

        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setColour(request.getColour());
        vehicle.setSeatCapacity(request.getSeatCapacity());
        vehicle.setStatus(request.getStatus());

        Vehicle updated = vehicleRepository.save(vehicle);
        return new VehicleResponse(updated);
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> getVehiclesByDriverId(Long driverId) {
        if (!driverProfileRepository.existsById(driverId)) {
            throw new ResourceNotFoundException("Driver profile not found with ID: " + driverId);
        }

        return vehicleRepository.findByDriverId(driverId).stream()
                .map(VehicleResponse::new)
                .collect(Collectors.toList());
    }
}
