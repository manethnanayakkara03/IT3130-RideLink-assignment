package com.ridelink.farepayment.repository;

import com.ridelink.farepayment.model.FareRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FareRecordRepository extends MongoRepository<FareRecord, String> {
    List<FareRecord> findByRideId(String rideId);
    Optional<FareRecord> findFirstByRideIdAndIsEstimateFalseOrderByCreatedAtDesc(String rideId);
}
