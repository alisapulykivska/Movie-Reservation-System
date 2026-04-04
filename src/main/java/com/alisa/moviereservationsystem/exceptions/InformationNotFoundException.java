package com.alisa.moviereservationsystem.exceptions;

public class InformationNotFoundException extends RuntimeException {
    public InformationNotFoundException(String message, Long id) {
        super(message + "not found with id " + id);
    }
}
