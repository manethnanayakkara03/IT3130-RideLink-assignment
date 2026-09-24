package com.ridelink.farepayment.repository;

import com.ridelink.farepayment.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findByPaymentId(Long paymentId);
    Optional<Receipt> findByRideId(Long rideId);
    Optional<Receipt> findByReceiptNumber(String receiptNumber);
}
