package com.ridelink.farepayment.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fare_records")
public class FareRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long rideId;

    @Column(nullable = false)
    private Double baseFare;

    @Column(nullable = false)
    private Double distanceKm;

    @Column(nullable = false)
    private Double distanceCharge;

    @Column(nullable = false)
    private Double durationMinutes;

    @Column(nullable = false)
    private Double timeCharge;

    @Column(nullable = false)
    private Double totalFare;

    @Column(nullable = false)
    private Boolean isEstimate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public FareRecord() {
    }

    public FareRecord(Long rideId, Double baseFare, Double distanceKm, Double distanceCharge,
                      Double durationMinutes, Double timeCharge, Double totalFare, Boolean isEstimate) {
        this.rideId = rideId;
        this.baseFare = baseFare;
        this.distanceKm = distanceKm;
        this.distanceCharge = distanceCharge;
        this.durationMinutes = durationMinutes;
        this.timeCharge = timeCharge;
        this.totalFare = totalFare;
        this.isEstimate = isEstimate;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
