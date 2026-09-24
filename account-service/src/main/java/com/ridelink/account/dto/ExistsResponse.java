package com.ridelink.account.dto;

import com.ridelink.account.model.AccountStatus;
import com.ridelink.account.model.Role;

public class ExistsResponse {
    private boolean exists;
    private Long id;
    private Role role;
    private AccountStatus status;

    public ExistsResponse() {
    }

    public ExistsResponse(boolean exists, Long id, Role role, AccountStatus status) {
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
