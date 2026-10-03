package com.ridelink.driver.dto;

public class AccountVerificationDto {
    private boolean exists;
    private String id;
    private String role;
    private String status;

    public AccountVerificationDto() {
    }

    public AccountVerificationDto(boolean exists, String id, String role, String status) {
        this.exists = exists;
        this.id = id;
        this.role = role;
        this.status = status;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
