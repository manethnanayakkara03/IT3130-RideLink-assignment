package com.ridelink.farepayment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FareCalculateRequest {

    @NotBlank(message = "Ride ID is required")
    private String rideId;

    @NotNull(message = "Actual distance in km is required")
    @DecimalMin(value = "0.1", message = "Distance must be greater than 0")
    private Double distanceKm;

    @NotNull(message = "Actual duration in minutes is required")
    @DecimalMin(value = "1.0", message = "Duration must be at least 1 minute")
    private Double durationMinutes;

    public FareCalculateRequest() {
    }

    public FareCalculateRequest(String rideId, Double distanceKm, Double durationMinutes) {
        this.rideId = rideId;
        this.distanceKm = distanceKm;
        this.durationMinutes = durationMinutes;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Double getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Double durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
