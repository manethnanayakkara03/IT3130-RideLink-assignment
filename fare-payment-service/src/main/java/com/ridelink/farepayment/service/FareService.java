package com.ridelink.farepayment.service;

import com.ridelink.farepayment.dto.FareCalculateRequest;
import com.ridelink.farepayment.dto.FareEstimateRequest;
import com.ridelink.farepayment.dto.FareResponse;
import com.ridelink.farepayment.model.FareRecord;
import com.ridelink.farepayment.repository.FareRecordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FareService {

    @Value("${ridelink.fare.base-fare:150.0}")
    private double baseFare;

    @Value("${ridelink.fare.rate-per-km:80.0}")
    private double ratePerKm;

    @Value("${ridelink.fare.rate-per-minute:5.0}")
    private double ratePerMinute;

    private final FareRecordRepository fareRecordRepository;

    public FareService(FareRecordRepository fareRecordRepository) {
        this.fareRecordRepository = fareRecordRepository;
    }

    public FareResponse estimateFare(FareEstimateRequest request) {
        double distanceCharge = round(request.getDistanceKm() * ratePerKm);
        double timeCharge = round(request.getEstimatedMinutes() * ratePerMinute);
        double totalFare = round(baseFare + distanceCharge + timeCharge);

        FareRecord record = new FareRecord(
                null,
                baseFare,
                request.getDistanceKm(),
                distanceCharge,
                request.getEstimatedMinutes(),
                timeCharge,
                totalFare,
                true
        );

        FareRecord saved = fareRecordRepository.save(record);
        return new FareResponse(saved);
    }

    public FareResponse calculateFinalFare(FareCalculateRequest request) {
        double distanceCharge = round(request.getDistanceKm() * ratePerKm);
        double timeCharge = round(request.getDurationMinutes() * ratePerMinute);
        double totalFare = round(baseFare + distanceCharge + timeCharge);

        FareRecord record = new FareRecord(
                request.getRideId(),
                baseFare,
                request.getDistanceKm(),
                distanceCharge,
                request.getDurationMinutes(),
                timeCharge,
                totalFare,
                false
        );

        FareRecord saved = fareRecordRepository.save(record);
        return new FareResponse(saved);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // Setters for unit testing
    public void setFareRates(double baseFare, double ratePerKm, double ratePerMinute) {
        this.baseFare = baseFare;
        this.ratePerKm = ratePerKm;
        this.ratePerMinute = ratePerMinute;
    }
}
