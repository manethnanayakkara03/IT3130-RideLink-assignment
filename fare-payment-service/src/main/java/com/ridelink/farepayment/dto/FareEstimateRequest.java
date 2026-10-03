package com.ridelink.farepayment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class FareEstimateRequest {

    @NotNull(message = "Distance in km is required")
    @DecimalMin(value = "0.1", message = "Distance must be greater than 0")
    private Double distanceKm;

    @NotNull(message = "Estimated duration in minutes is required")
    @DecimalMin(value = "1.0", message = "Estimated duration must be at least 1 minute")
    private Double estimatedMinutes;

    public FareEstimateRequest() {
    }

    public FareEstimateRequest(Double distanceKm, Double estimatedMinutes) {
        this.distanceKm = distanceKm;
        this.estimatedMinutes = estimatedMinutes;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Double getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Double estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }
}
