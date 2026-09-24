package com.ridelink.farepayment.repository;

import com.ridelink.farepayment.model.FareRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FareRecordRepository extends JpaRepository<FareRecord, Long> {
    List<FareRecord> findByRideId(Long rideId);
    Optional<FareRecord> findFirstByRideIdAndIsEstimateFalseOrderByCreatedAtDesc(Long rideId);
}
