package com.ridelink.driver.controller;

import com.ridelink.driver.dto.*;
import com.ridelink.driver.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@Tag(name = "Drivers", description = "Endpoints for driver operational profiles, availability, and location management")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    @Operation(summary = "Create driver operational profile", description = "Registers operational details for an existing DRIVER account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Driver profile created successfully",
                    content = @Content(schema = @Schema(implementation = DriverProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or account is not a DRIVER"),
            @ApiResponse(responseCode = "404", description = "Account ID not found in Account Service"),
            @ApiResponse(responseCode = "409", description = "Driver profile or license number already exists")
    })
    public ResponseEntity<DriverProfileResponse> createDriverProfile(@Valid @RequestBody CreateDriverProfileRequest request) {
        DriverProfileResponse response = driverService.createDriverProfile(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get driver profile by ID", description = "Retrieves operational details of a driver")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver profile found",
                    content = @Content(schema = @Schema(implementation = DriverProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "Driver profile not found")
    })
    public ResponseEntity<DriverProfileResponse> getDriverById(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update driver profile", description = "Updates driver license number and service area")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver profile updated successfully",
                    content = @Content(schema = @Schema(implementation = DriverProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "Driver profile not found"),
            @ApiResponse(responseCode = "409", description = "License number already registered by another driver")
    })
    public ResponseEntity<DriverProfileResponse> updateDriver(@PathVariable Long id,
                                                              @Valid @RequestBody UpdateDriverProfileRequest request) {
        return ResponseEntity.ok(driverService.updateDriver(id, request));
    }

    @PatchMapping("/{id}/availability")
    @Operation(summary = "Update driver availability", description = "Updates driver availability state to AVAILABLE, UNAVAILABLE, or BUSY")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Availability updated successfully",
                    content = @Content(schema = @Schema(implementation = DriverProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "Driver profile not found")
    })
    public ResponseEntity<DriverProfileResponse> updateAvailability(@PathVariable Long id,
                                                                    @Valid @RequestBody UpdateAvailabilityRequest request) {
        return ResponseEntity.ok(driverService.updateAvailability(id, request.getStatus()));
    }

    @PatchMapping("/{id}/location")
    @Operation(summary = "Update driver simulated location", description = "Updates driver latitude, longitude, and optional service area")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location updated successfully",
                    content = @Content(schema = @Schema(implementation = DriverProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "Driver profile not found")
    })
    public ResponseEntity<DriverProfileResponse> updateLocation(@PathVariable Long id,
                                                                @Valid @RequestBody UpdateLocationRequest request) {
        return ResponseEntity.ok(driverService.updateLocation(id, request));
    }

    @GetMapping("/available")
    @Operation(summary = "Get available drivers", description = "Interservice & public endpoint to retrieve available eligible drivers, optionally filtered by serviceArea")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of available drivers returned",
                    content = @Content(schema = @Schema(implementation = DriverProfileResponse.class)))
    })
    public ResponseEntity<List<DriverProfileResponse>> getAvailableDrivers(
            @RequestParam(required = false) String serviceArea) {
        return ResponseEntity.ok(driverService.getAvailableDrivers(serviceArea));
    }
}
