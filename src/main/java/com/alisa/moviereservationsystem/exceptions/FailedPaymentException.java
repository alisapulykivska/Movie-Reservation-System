package com.alisa.moviereservationsystem.exceptions;

public class FailedPaymentException extends Exception {
    public FailedPaymentException(String message) {
        super(message);
    }
}
