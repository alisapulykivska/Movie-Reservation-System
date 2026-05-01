package com.alisa.moviereservationsystem.exceptions;

public class InformationNotFoundException extends RuntimeException {
    public InformationNotFoundException(String message) {
        super(message);
    }
}
