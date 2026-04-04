package com.alisa.moviereservationsystem.advice;

import com.alisa.moviereservationsystem.dto.errorDto.ErrorResponse;
import com.alisa.moviereservationsystem.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> bookedHall(BookedHallException e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), 400, Instant.now()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> informationIsNull(InformationIsNullException e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), 400, Instant.now()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> invalidReservationStatus(InvalidReservationStatusException e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), 400, Instant.now()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> pastShowtime(PastShowtimeException e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), 400, Instant.now()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> informationNotFound(InformationNotFoundException e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), 404, Instant.now()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> seatsUnavailable(SeatsUnavailableException e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), 400, Instant.now()),
                HttpStatus.BAD_REQUEST
        );
    }
}
