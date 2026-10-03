package com.ridelink.ride.dto;

public class FareCalculateRequestDto {
    private String rideId;
    private Double distanceKm;
    private Double durationMinutes;

    public FareCalculateRequestDto() {
    }

    public FareCalculateRequestDto(String rideId, Double distanceKm, Double durationMinutes) {
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
