package com.ridelink.account.controller;

import com.ridelink.account.dto.AccountResponse;
import com.ridelink.account.dto.AccountStatusUpdateRequest;
import com.ridelink.account.dto.AccountUpdateRequest;
import com.ridelink.account.dto.ExistsResponse;
import com.ridelink.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "Endpoints for managing user account profiles and status")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID", description = "Retrieves profile details of a registered account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update account profile", description = "Updates first name, last name, and phone number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account updated successfully",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable Long id,
                                                         @Valid @RequestBody AccountUpdateRequest request) {
        return ResponseEntity.ok(accountService.updateAccount(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update account status", description = "Admin endpoint to activate, deactivate, or suspend an account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account status updated",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<AccountResponse> updateAccountStatus(@PathVariable Long id,
                                                               @Valid @RequestBody AccountStatusUpdateRequest request) {
        return ResponseEntity.ok(accountService.updateAccountStatus(id, request.getStatus()));
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Verify account exists (Interservice)", description = "Inter-service verification endpoint called by Driver Service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification result returned",
                    content = @Content(schema = @Schema(implementation = ExistsResponse.class)))
    })
    public ResponseEntity<ExistsResponse> verifyAccountExists(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.verifyAccountExists(id));
    }
}
