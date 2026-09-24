package com.ridelink.farepayment.controller;

import com.ridelink.farepayment.dto.ReceiptResponse;
import com.ridelink.farepayment.service.ReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/receipts")
@Tag(name = "Receipts", description = "Endpoints for retrieving ride payment receipts")
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get receipt by payment ID", description = "Retrieves payment receipt by payment ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receipt found",
                    content = @Content(schema = @Schema(implementation = ReceiptResponse.class))),
            @ApiResponse(responseCode = "404", description = "Receipt not found for payment")
    })
    public ResponseEntity<ReceiptResponse> getReceiptByPaymentId(@PathVariable Long paymentId) {
        return ResponseEntity.ok(receiptService.getReceiptByPaymentId(paymentId));
    }

    @GetMapping("/ride/{rideId}")
    @Operation(summary = "Get receipt by ride ID", description = "Retrieves payment receipt by ride ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receipt found",
                    content = @Content(schema = @Schema(implementation = ReceiptResponse.class))),
            @ApiResponse(responseCode = "404", description = "Receipt not found for ride")
    })
    public ResponseEntity<ReceiptResponse> getReceiptByRideId(@PathVariable Long rideId) {
        return ResponseEntity.ok(receiptService.getReceiptByRideId(rideId));
    }
}
