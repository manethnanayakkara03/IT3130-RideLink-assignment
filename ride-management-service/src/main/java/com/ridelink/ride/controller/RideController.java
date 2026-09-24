package com.ridelink.ride.controller;

import com.ridelink.ride.dto.CancelRideRequest;
import com.ridelink.ride.dto.CreateRideRequest;
import com.ridelink.ride.dto.RideResponse;
import com.ridelink.ride.service.RideService;
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
@RequestMapping("/api/rides")
@Tag(name = "Rides", description = "Endpoints for creating rides, driver assignment, state lifecycle transitions, and history")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping
    @Operation(summary = "Create ride request", description = "Creates a new ride in REQUESTED state and fetches initial fare estimate from Fare Service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ride requested successfully",
                    content = @Content(schema = @Schema(implementation = RideResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request coordinates or parameters")
    })
    public ResponseEntity<RideResponse> createRide(@Valid @RequestBody CreateRideRequest request) {
        RideResponse response = rideService.createRide(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ride by ID", description = "Retrieves ride details and current lifecycle status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ride found",
                    content = @Content(schema = @Schema(implementation = RideResponse.class))),
            @ApiResponse(responseCode = "404", description = "Ride not found")
    })
    public ResponseEntity<RideResponse> getRideById(@PathVariable Long id) {
        return ResponseEntity.ok(rideService.getRideById(id));
    }

    @PostMapping("/{id}/assign")
    @Operation(summary = "Assign eligible driver", description = "Inter-service call to Driver Service to find available driver and assign to ride")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver assigned successfully",
                    content = @Content(schema = @Schema(implementation = RideResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ride state for assignment"),
            @ApiResponse(responseCode = "404", description = "No available driver found or ride not found")
    })
    public ResponseEntity<RideResponse> assignDriver(@PathVariable Long id) {
        return ResponseEntity.ok(rideService.assignDriver(id));
    }

    @PostMapping("/{id}/accept")
    @Operation(summary = "Driver accepts ride", description = "Transitions ride state from ASSIGNED to ACCEPTED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ride accepted",
                    content = @Content(schema = @Schema(implementation = RideResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid transition: ride is not in ASSIGNED state"),
            @ApiResponse(responseCode = "404", description = "Ride not found")
    })
    public ResponseEntity<RideResponse> acceptRide(@PathVariable Long id) {
        return ResponseEntity.ok(rideService.acceptRide(id));
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "Driver starts ride", description = "Transitions ride state from ACCEPTED to IN_PROGRESS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ride started",
                    content = @Content(schema = @Schema(implementation = RideResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid transition: ride is not in ACCEPTED state"),
            @ApiResponse(responseCode = "404", description = "Ride not found")
    })
    public ResponseEntity<RideResponse> startRide(@PathVariable Long id) {
        return ResponseEntity.ok(rideService.startRide(id));
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Driver completes ride", description = "Transitions ride state to COMPLETED and calls Fare Service for final calculation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ride completed",
                    content = @Content(schema = @Schema(implementation = RideResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid transition: ride is not in IN_PROGRESS state"),
            @ApiResponse(responseCode = "404", description = "Ride not found")
    })
    public ResponseEntity<RideResponse> completeRide(@PathVariable Long id) {
        return ResponseEntity.ok(rideService.completeRide(id));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel ride", description = "Cancels ride if in valid cancellable state (REQUESTED, ASSIGNED, ACCEPTED)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ride cancelled successfully",
                    content = @Content(schema = @Schema(implementation = RideResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid transition: completed or in-progress rides cannot be cancelled"),
            @ApiResponse(responseCode = "404", description = "Ride not found")
    })
    public ResponseEntity<RideResponse> cancelRide(@PathVariable Long id,
                                                   @Valid @RequestBody CancelRideRequest request) {
        return ResponseEntity.ok(rideService.cancelRide(id, request));
    }

    @GetMapping("/passenger/{passengerId}")
    @Operation(summary = "Get ride history for passenger", description = "Retrieves all rides requested by a passenger")
    public ResponseEntity<List<RideResponse>> getRidesByPassengerId(@PathVariable Long passengerId) {
        return ResponseEntity.ok(rideService.getRidesByPassengerId(passengerId));
    }

    @GetMapping("/driver/{driverId}")
    @Operation(summary = "Get ride history for driver", description = "Retrieves all rides assigned to a driver")
    public ResponseEntity<List<RideResponse>> getRidesByDriverId(@PathVariable Long driverId) {
        return ResponseEntity.ok(rideService.getRidesByDriverId(driverId));
    }
}
