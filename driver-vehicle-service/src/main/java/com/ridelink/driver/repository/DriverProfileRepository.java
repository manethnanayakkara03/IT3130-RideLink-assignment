package com.ridelink.driver.repository;

import com.ridelink.driver.model.AvailabilityStatus;
import com.ridelink.driver.model.DriverProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverProfileRepository extends MongoRepository<DriverProfile, String> {
    Optional<DriverProfile> findByAccountId(String accountId);
    Optional<DriverProfile> findByLicenseNumber(String licenseNumber);
    boolean existsByAccountId(String accountId);
    boolean existsByLicenseNumber(String licenseNumber);
    List<DriverProfile> findByAvailabilityStatus(AvailabilityStatus status);
    List<DriverProfile> findByAvailabilityStatusAndServiceAreaIgnoreCase(AvailabilityStatus status, String serviceArea);
}
