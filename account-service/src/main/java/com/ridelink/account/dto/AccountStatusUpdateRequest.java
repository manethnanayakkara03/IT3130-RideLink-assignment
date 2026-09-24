package com.ridelink.account.dto;

import com.ridelink.account.model.AccountStatus;
import jakarta.validation.constraints.NotNull;

public class AccountStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private AccountStatus status;

    public AccountStatusUpdateRequest() {
    }

    public AccountStatusUpdateRequest(AccountStatus status) {
        this.status = status;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
