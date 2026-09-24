package com.ridelink.account.controller;

import com.ridelink.account.dto.*;
import com.ridelink.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for passenger/driver registration and JWT authentication")
public class AuthController {

    private final AccountService accountService;

    public AuthController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/register/passenger")
    @Operation(summary = "Register a new passenger", description = "Registers a passenger account with hashed password and role PASSENGER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Passenger registered successfully",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request validation errors"),
            @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    public ResponseEntity<AccountResponse> registerPassenger(@Valid @RequestBody RegisterPassengerRequest request) {
        AccountResponse response = accountService.registerPassenger(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/register/driver")
    @Operation(summary = "Register a new driver", description = "Registers a driver user account with hashed password and role DRIVER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Driver account registered successfully",
                    content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request validation errors"),
            @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    public ResponseEntity<AccountResponse> registerDriver(@Valid @RequestBody RegisterDriverRequest request) {
        AccountResponse response = accountService.registerDriver(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user credentials and returns a signed JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid email or password")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = accountService.login(request);
        return ResponseEntity.ok(response);
    }
}
