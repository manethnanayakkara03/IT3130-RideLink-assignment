package com.ridelink.farepayment.exception;

public class InvalidFareRequestException extends RuntimeException {
    public InvalidFareRequestException(String message) {
        super(message);
    }
}
