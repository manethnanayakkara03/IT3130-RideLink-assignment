package com.ridelink.farepayment.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "fare_records")
public class FareRecord {

    @Id
    private String id;

    private String rideId;
    private Double baseFare;
    private Double distanceKm;
    private Double distanceCharge;
    private Double durationMinutes;
    private Double timeCharge;
    private Double totalFare;
    private Boolean isEstimate;
    private LocalDateTime createdAt;

    public FareRecord() {
        this.createdAt = LocalDateTime.now();
    }

    public FareRecord(String rideId, Double baseFare, Double distanceKm, Double distanceCharge,
                      Double durationMinutes, Double timeCharge, Double totalFare, Boolean isEstimate) {
        this.rideId = rideId;
        this.baseFare = baseFare;
        this.distanceKm = distanceKm;
        this.distanceCharge = distanceCharge;
        this.durationMinutes = durationMinutes;
        this.timeCharge = timeCharge;
        this.totalFare = totalFare;
        this.isEstimate = isEstimate;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
