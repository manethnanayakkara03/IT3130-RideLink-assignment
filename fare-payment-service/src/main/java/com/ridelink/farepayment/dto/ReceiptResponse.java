package com.ridelink.farepayment.dto;

import com.ridelink.farepayment.model.Receipt;

import java.time.LocalDateTime;

public class ReceiptResponse {
    private Long id;
    private Long paymentId;
    private Long rideId;
    private String receiptNumber;
    private Double amount;
    private LocalDateTime issuedAt;
    private String details;

    public ReceiptResponse() {
    }

    public ReceiptResponse(Receipt receipt) {
        this.id = receipt.getId();
        this.paymentId = receipt.getPaymentId();
        this.rideId = receipt.getRideId();
        this.receiptNumber = receipt.getReceiptNumber();
        this.amount = receipt.getAmount();
        this.issuedAt = receipt.getIssuedAt();
        this.details = receipt.getDetails();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
