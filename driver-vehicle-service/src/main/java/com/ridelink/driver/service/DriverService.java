package com.ridelink.driver.service;

import com.ridelink.driver.client.AccountServiceClient;
import com.ridelink.driver.dto.*;
import com.ridelink.driver.exception.DuplicateResourceException;
import com.ridelink.driver.exception.InvalidDriverStateException;
import com.ridelink.driver.exception.ResourceNotFoundException;
import com.ridelink.driver.model.AvailabilityStatus;
import com.ridelink.driver.model.DriverProfile;
import com.ridelink.driver.repository.DriverProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DriverService {

    private final DriverProfileRepository driverProfileRepository;
    private final AccountServiceClient accountServiceClient;

    public DriverService(DriverProfileRepository driverProfileRepository,
                         AccountServiceClient accountServiceClient) {
        this.driverProfileRepository = driverProfileRepository;
        this.accountServiceClient = accountServiceClient;
    }

    public DriverProfileResponse createDriverProfile(CreateDriverProfileRequest request) {
        if (driverProfileRepository.existsByAccountId(request.getAccountId())) {
            throw new DuplicateResourceException("A driver profile already exists for account ID: " + request.getAccountId());
        }

        if (driverProfileRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateResourceException("License number " + request.getLicenseNumber() + " is already registered");
        }

        // Verify with Account Service via REST
        AccountVerificationDto verification = accountServiceClient.verifyAccount(request.getAccountId());
        if (verification != null) {
            if (!verification.isExists()) {
                throw new ResourceNotFoundException("Account does not exist in Account Service for ID: " + request.getAccountId());
            }
            if (!"DRIVER".equalsIgnoreCase(verification.getRole())) {
                throw new InvalidDriverStateException("Account ID " + request.getAccountId() + " is not registered with role DRIVER");
            }
        }

        DriverProfile profile = new DriverProfile(
                request.getAccountId(),
                request.getLicenseNumber(),
                AvailabilityStatus.AVAILABLE,
                request.getServiceArea(),
                request.getLatitude(),
                request.getLongitude()
        );

        DriverProfile saved = driverProfileRepository.save(profile);
        return new DriverProfileResponse(saved);
    }

    @Transactional(readOnly = true)
    public DriverProfileResponse getDriverById(Long id) {
        DriverProfile profile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found with ID: " + id));
        return new DriverProfileResponse(profile);
    }

    public DriverProfileResponse updateDriver(Long id, UpdateDriverProfileRequest request) {
        DriverProfile profile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found with ID: " + id));

        if (!profile.getLicenseNumber().equalsIgnoreCase(request.getLicenseNumber())
                && driverProfileRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateResourceException("License number " + request.getLicenseNumber() + " is already registered");
        }

        profile.setLicenseNumber(request.getLicenseNumber());
        profile.setServiceArea(request.getServiceArea());

        DriverProfile updated = driverProfileRepository.save(profile);
        return new DriverProfileResponse(updated);
    }

    public DriverProfileResponse updateAvailability(Long id, AvailabilityStatus status) {
        DriverProfile profile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found with ID: " + id));

        profile.setAvailabilityStatus(status);
        DriverProfile updated = driverProfileRepository.save(profile);
        return new DriverProfileResponse(updated);
    }

    public DriverProfileResponse updateLocation(Long id, UpdateLocationRequest request) {
        DriverProfile profile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found with ID: " + id));

        profile.setLatitude(request.getLatitude());
        profile.setLongitude(request.getLongitude());
        if (request.getServiceArea() != null && !request.getServiceArea().isBlank()) {
            profile.setServiceArea(request.getServiceArea());
        }

        DriverProfile updated = driverProfileRepository.save(profile);
        return new DriverProfileResponse(updated);
    }

    @Transactional(readOnly = true)
    public List<DriverProfileResponse> getAvailableDrivers(String serviceArea) {
        List<DriverProfile> drivers;
        if (serviceArea != null && !serviceArea.isBlank()) {
            drivers = driverProfileRepository.findByAvailabilityStatusAndServiceAreaIgnoreCase(
                    AvailabilityStatus.AVAILABLE, serviceArea.trim()
            );
        } else {
            drivers = driverProfileRepository.findByAvailabilityStatus(AvailabilityStatus.AVAILABLE);
        }

        return drivers.stream()
                .map(DriverProfileResponse::new)
                .collect(Collectors.toList());
    }
}
