package com.ridelink.driver.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateDriverProfileRequest {

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @NotBlank(message = "Service area is required")
    private String serviceArea;

    public UpdateDriverProfileRequest() {
    }

    public UpdateDriverProfileRequest(String licenseNumber, String serviceArea) {
        this.licenseNumber = licenseNumber;
        this.serviceArea = serviceArea;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getServiceArea() {
        return serviceArea;
    }

    public void setServiceArea(String serviceArea) {
        this.serviceArea = serviceArea;
    }
}
