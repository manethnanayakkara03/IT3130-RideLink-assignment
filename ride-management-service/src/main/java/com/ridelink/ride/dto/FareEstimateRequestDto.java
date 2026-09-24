package com.ridelink.ride.dto;

public class FareEstimateRequestDto {
    private Double distanceKm;
    private Double estimatedMinutes;

    public FareEstimateRequestDto() {
    }

    public FareEstimateRequestDto(Double distanceKm, Double estimatedMinutes) {
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
