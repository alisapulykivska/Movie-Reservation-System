package com.alisa.moviereservationsystem.exceptions;

public class PastShowtimeException extends RuntimeException {
    public PastShowtimeException(String message) {
        super(message);
    }
}
