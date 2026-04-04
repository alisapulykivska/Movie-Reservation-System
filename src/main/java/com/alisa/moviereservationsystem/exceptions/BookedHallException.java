package com.alisa.moviereservationsystem.exceptions;

public class BookedHallException extends RuntimeException {
    public BookedHallException(String message) {
        super(message);
    }
}
