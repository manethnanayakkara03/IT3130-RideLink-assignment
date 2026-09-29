package com.ridelink.farepayment.service;

import com.ridelink.farepayment.dto.FareCalculateRequest;
import com.ridelink.farepayment.dto.FareEstimateRequest;
import com.ridelink.farepayment.dto.FareResponse;
import com.ridelink.farepayment.model.FareRecord;
import com.ridelink.farepayment.repository.FareRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for FareService.
 * Primary Owner: Priyamalka W.D.N (IT24102758)
 */
@ExtendWith(MockitoExtension.class)
class FareServiceTest {

    @Mock
    private FareRecordRepository fareRecordRepository;

    @InjectMocks
    private FareService fareService;

    @BeforeEach
    void setUp() {
        fareService.setFareRates(150.0, 80.0, 5.0);
    }

    @Test
    @DisplayName("Should accurately estimate fare: baseFare + (distance * ratePerKm) + (time * ratePerMin)")
    void testEstimateFare_CalculationAccuracy() {
        // Distance: 10 km, Time: 20 min
        // Formula: 150 + (10 * 80) + (20 * 5) = 150 + 800 + 100 = 1050.0
        FareEstimateRequest request = new FareEstimateRequest(10.0, 20.0);

        when(fareRecordRepository.save(any(FareRecord.class))).thenAnswer(invocation -> {
            FareRecord record = invocation.getArgument(0);
            record.setId("fare-est-001");
            return record;
        });

        FareResponse response = fareService.estimateFare(request);

        assertNotNull(response);
        assertEquals(150.0, response.getBaseFare());
        assertEquals(10.0, response.getDistanceKm());
        assertEquals(800.0, response.getDistanceCharge());
        assertEquals(20.0, response.getDurationMinutes());
        assertEquals(100.0, response.getTimeCharge());
        assertEquals(1050.0, response.getTotalFare());
        assertTrue(response.getIsEstimate());
    }

    @Test
    @DisplayName("Should accurately calculate final fare for completed ride")
    void testCalculateFinalFare_Success() {
        // Ride: ride-005, Distance: 15.5 km, Time: 30 min
        // Formula: 150 + (15.5 * 80) + (30 * 5) = 150 + 1240 + 150 = 1540.0
        FareCalculateRequest request = new FareCalculateRequest("ride-005", 15.5, 30.0);

        when(fareRecordRepository.save(any(FareRecord.class))).thenAnswer(invocation -> {
            FareRecord record = invocation.getArgument(0);
            record.setId("fare-rec-002");
            return record;
        });

        FareResponse response = fareService.calculateFinalFare(request);

        assertNotNull(response);
        assertEquals("ride-005", response.getRideId());
        assertEquals(1540.0, response.getTotalFare());
        assertFalse(response.getIsEstimate());
    }
}
