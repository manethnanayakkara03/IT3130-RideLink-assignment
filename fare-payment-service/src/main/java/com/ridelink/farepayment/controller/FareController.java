package com.ridelink.farepayment.controller;

import com.ridelink.farepayment.dto.FareCalculateRequest;
import com.ridelink.farepayment.dto.FareEstimateRequest;
import com.ridelink.farepayment.dto.FareResponse;
import com.ridelink.farepayment.service.FareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fares")
@Tag(name = "Fares", description = "Endpoints for pre-ride fare estimation and post-ride final fare calculation")
public class FareController {

    private final FareService fareService;

    public FareController(FareService fareService) {
        this.fareService = fareService;
    }

    @PostMapping("/estimate")
    @Operation(summary = "Estimate fare", description = "Calculates estimated fare based on distance (km) and estimated duration (minutes)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fare estimated successfully",
                    content = @Content(schema = @Schema(implementation = FareResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request coordinates or parameters")
    })
    public ResponseEntity<FareResponse> estimateFare(@Valid @RequestBody FareEstimateRequest request) {
        return ResponseEntity.ok(fareService.estimateFare(request));
    }

    @PostMapping("/final")
    @Operation(summary = "Calculate final fare", description = "Calculates final fare for completed ride and persists fare breakdown record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Final fare calculated successfully",
                    content = @Content(schema = @Schema(implementation = FareResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid calculation parameters")
    })
    public ResponseEntity<FareResponse> calculateFinalFare(@Valid @RequestBody FareCalculateRequest request) {
        return ResponseEntity.ok(fareService.calculateFinalFare(request));
    }
}
