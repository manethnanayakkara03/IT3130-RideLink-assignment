package com.ridelink.farepayment.dto;

import com.ridelink.farepayment.model.FareRecord;

public class FareResponse {
    private Long id;
    private Long rideId;
    private Double baseFare;
    private Double distanceKm;
    private Double distanceCharge;
    private Double durationMinutes;
    private Double timeCharge;
    private Double totalFare;
    private Boolean isEstimate;

    public FareResponse() {
    }

    public FareResponse(FareRecord record) {
        this.id = record.getId();
        this.rideId = record.getRideId();
        this.baseFare = record.getBaseFare();
        this.distanceKm = record.getDistanceKm();
        this.distanceCharge = record.getDistanceCharge();
        this.durationMinutes = record.getDurationMinutes();
        this.timeCharge = record.getTimeCharge();
        this.totalFare = record.getTotalFare();
        this.isEstimate = record.getIsEstimate();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public Double getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(Double baseFare) {
        this.baseFare = baseFare;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Double getDistanceCharge() {
        return distanceCharge;
    }

    public void setDistanceCharge(Double distanceCharge) {
        this.distanceCharge = distanceCharge;
    }

    public Double getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Double durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Double getTimeCharge() {
        return timeCharge;
    }

    public void setTimeCharge(Double timeCharge) {
        this.timeCharge = timeCharge;
    }

    public Double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(Double totalFare) {
        this.totalFare = totalFare;
    }

    public Boolean getIsEstimate() {
        return isEstimate;
    }

    public void setIsEstimate(Boolean isEstimate) {
        this.isEstimate = isEstimate;
    }
}
