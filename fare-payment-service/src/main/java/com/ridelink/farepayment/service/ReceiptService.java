package com.ridelink.farepayment.service;

import com.ridelink.farepayment.dto.ReceiptResponse;
import com.ridelink.farepayment.exception.ResourceNotFoundException;
import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.model.Receipt;
import com.ridelink.farepayment.repository.ReceiptRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class ReceiptService {

    private final ReceiptRepository receiptRepository;

    public ReceiptService(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    public Receipt generateReceipt(Payment payment, String rideId) {
        String receiptNumber = "REC-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        String details = String.format("RideLink E-Receipt: Ride #%s | Passenger #%s | Amount: LKR %.2f | Method: %s | Ref: %s",
                rideId, payment.getPassengerId(), payment.getAmount(), payment.getPaymentMethod(), payment.getTransactionReference());

        Receipt receipt = new Receipt(
                payment.getId(),
                rideId,
                receiptNumber,
                payment.getAmount(),
                LocalDateTime.now(),
                details
        );

        return receiptRepository.save(receipt);
    }

    public ReceiptResponse getReceiptByPaymentId(String paymentId) {
        Receipt receipt = receiptRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found for payment ID: " + paymentId));
        return new ReceiptResponse(receipt);
    }

    public ReceiptResponse getReceiptByRideId(String rideId) {
        Receipt receipt = receiptRepository.findByRideId(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found for ride ID: " + rideId));
        return new ReceiptResponse(receipt);
    }
}
