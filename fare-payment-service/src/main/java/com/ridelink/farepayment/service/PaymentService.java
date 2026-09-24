package com.ridelink.farepayment.service;

import com.ridelink.farepayment.dto.PaymentResponse;
import com.ridelink.farepayment.dto.SimulatePaymentRequest;
import com.ridelink.farepayment.exception.PaymentException;
import com.ridelink.farepayment.exception.ResourceNotFoundException;
import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.model.PaymentStatus;
import com.ridelink.farepayment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReceiptService receiptService;

    public PaymentService(PaymentRepository paymentRepository,
                          ReceiptService receiptService) {
        this.paymentRepository = paymentRepository;
        this.receiptService = receiptService;
    }

    public PaymentResponse processPayment(SimulatePaymentRequest request) {
        String ref = "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        Payment payment = new Payment(
                request.getRideId(),
                request.getPassengerId(),
                request.getAmount(),
                request.getPaymentMethod(),
                PaymentStatus.PENDING,
                ref
        );

        if (request.isSimulateFailure()) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setSimulatedFailureReason("Simulated card payment authorization declined by issuer");
            Payment saved = paymentRepository.save(payment);
            return new PaymentResponse(saved);
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);

        // Auto-generate receipt upon successful payment
        receiptService.generateReceipt(saved, request.getRideId());

        return new PaymentResponse(saved);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found with ID: " + id));
        return new PaymentResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByRideId(Long rideId) {
        Payment payment = paymentRepository.findFirstByRideIdOrderByCreatedAtDesc(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found for ride ID: " + rideId));
        return new PaymentResponse(payment);
    }
}
