package com.ridelink.driver.dto;

import com.ridelink.driver.model.Vehicle;
import com.ridelink.driver.model.VehicleStatus;

import java.time.LocalDateTime;

public class VehicleResponse {
    private Long id;
    private Long driverId;
    private String registrationNumber;
    private String make;
    private String model;
    private String vehicleType;
    private String colour;
    private Integer seatCapacity;
    private VehicleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public VehicleResponse() {
    }

    public VehicleResponse(Vehicle vehicle) {
        this.id = vehicle.getId();
        this.driverId = vehicle.getDriverId();
        this.registrationNumber = vehicle.getRegistrationNumber();
        this.make = vehicle.getMake();
        this.model = vehicle.getModel();
        this.vehicleType = vehicle.getVehicleType();
        this.colour = vehicle.getColour();
        this.seatCapacity = vehicle.getSeatCapacity();
        this.status = vehicle.getStatus();
        this.createdAt = vehicle.getCreatedAt();
        this.updatedAt = vehicle.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public Integer getSeatCapacity() {
        return seatCapacity;
    }

    public void setSeatCapacity(Integer seatCapacity) {
        this.seatCapacity = seatCapacity;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
