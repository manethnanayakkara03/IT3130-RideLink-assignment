package com.ridelink.driver.dto;

public class AccountVerificationDto {
    private boolean exists;
    private Long id;
    private String role;
    private String status;

    public AccountVerificationDto() {
    }

    public AccountVerificationDto(boolean exists, Long id, String role, String status) {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
