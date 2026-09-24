package com.ridelink.farepayment.repository;

import com.ridelink.farepayment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByRideId(Long rideId);
    Optional<Payment> findByTransactionReference(String transactionReference);
    Optional<Payment> findFirstByRideIdOrderByCreatedAtDesc(Long rideId);
}
