package com.ridelink.ride.dto;

public class FareCalculateRequestDto {
    private Long rideId;
    private Double distanceKm;
    private Double durationMinutes;

    public FareCalculateRequestDto() {
    }

    public FareCalculateRequestDto(Long rideId, Double distanceKm, Double durationMinutes) {
        this.rideId = rideId;
        this.distanceKm = distanceKm;
        this.durationMinutes = durationMinutes;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
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
