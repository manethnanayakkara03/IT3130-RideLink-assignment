package com.ridelink.farepayment.service;

import com.ridelink.farepayment.dto.PaymentResponse;
import com.ridelink.farepayment.dto.SimulatePaymentRequest;
import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.model.PaymentStatus;
import com.ridelink.farepayment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for PaymentService.
 * Primary Owner: Priyamalka W.D.N (IT24102758)
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ReceiptService receiptService;

    @InjectMocks
    private PaymentService paymentService;

    private Payment samplePayment;

    @BeforeEach
    void setUp() {
        samplePayment = new Payment(1L, 100L, 850.0, "SIMULATED_CARD", PaymentStatus.SUCCESS, "TXN-TEST-12345");
        samplePayment.setId(10L);
        samplePayment.setPaidAt(LocalDateTime.now());
        samplePayment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should successfully process simulated payment and generate receipt")
    void testProcessPayment_Success() {
        SimulatePaymentRequest request = new SimulatePaymentRequest(
                1L, 100L, 850.0, "SIMULATED_CARD", false
        );

        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            p.setId(10L);
            return p;
        });

        PaymentResponse response = paymentService.processPayment(request);

        assertNotNull(response);
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
        assertEquals(850.0, response.getAmount());
        assertNotNull(response.getPaidAt());
        assertNotNull(response.getTransactionReference());
        verify(receiptService, times(1)).generateReceipt(any(Payment.class), eq(1L));
    }

    @Test
    @DisplayName("Negative Scenario: Should record FAILED payment when simulateFailure is true")
    void testProcessPayment_SimulatedFailure() {
        SimulatePaymentRequest request = new SimulatePaymentRequest(
                1L, 100L, 850.0, "SIMULATED_CARD", true
        );

        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            p.setId(11L);
            return p;
        });

        PaymentResponse response = paymentService.processPayment(request);

        assertNotNull(response);
        assertEquals(PaymentStatus.FAILED, response.getStatus());
        assertNotNull(response.getSimulatedFailureReason());
        assertNull(response.getPaidAt());
        verify(receiptService, never()).generateReceipt(any(Payment.class), anyLong());
    }

    @Test
    @DisplayName("Should retrieve payment by ID")
    void testGetPaymentById_Success() {
        when(paymentRepository.findById(10L)).thenReturn(Optional.of(samplePayment));

        PaymentResponse response = paymentService.getPaymentById(10L);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("TXN-TEST-12345", response.getTransactionReference());
    }
}
