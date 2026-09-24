package com.ridelink.driver.controller;

import com.ridelink.driver.dto.CreateVehicleRequest;
import com.ridelink.driver.dto.UpdateVehicleRequest;
import com.ridelink.driver.dto.VehicleResponse;
import com.ridelink.driver.service.VehicleService;
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
@RequestMapping
@Tag(name = "Vehicles", description = "Endpoints for vehicle registration and management")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping("/api/vehicles")
    @Operation(summary = "Register vehicle", description = "Registers a vehicle for an operational driver")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vehicle registered successfully",
                    content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request validation errors"),
            @ApiResponse(responseCode = "404", description = "Driver profile not found"),
            @ApiResponse(responseCode = "409", description = "Vehicle registration number already exists")
    })
    public ResponseEntity<VehicleResponse> registerVehicle(@Valid @RequestBody CreateVehicleRequest request) {
        VehicleResponse response = vehicleService.registerVehicle(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/api/vehicles/{id}")
    @Operation(summary = "Get vehicle by ID", description = "Retrieves vehicle details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle found",
                    content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @PutMapping("/api/vehicles/{id}")
    @Operation(summary = "Update vehicle", description = "Updates vehicle specifications and status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle updated successfully",
                    content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
            @ApiResponse(responseCode = "404", description = "Vehicle not found")
    })
    public ResponseEntity<VehicleResponse> updateVehicle(@PathVariable Long id,
                                                         @Valid @RequestBody UpdateVehicleRequest request) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, request));
    }

    @GetMapping("/api/drivers/{driverId}/vehicles")
    @Operation(summary = "Get vehicles of driver", description = "Retrieves all vehicles associated with a driver profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicles retrieved",
                    content = @Content(schema = @Schema(implementation = VehicleResponse.class))),
            @ApiResponse(responseCode = "404", description = "Driver profile not found")
    })
    public ResponseEntity<List<VehicleResponse>> getVehiclesByDriverId(@PathVariable Long driverId) {
        return ResponseEntity.ok(vehicleService.getVehiclesByDriverId(driverId));
    }
}
