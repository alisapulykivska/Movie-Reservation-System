package com.alisa.moviereservationsystem.advice;

import com.alisa.moviereservationsystem.dto.errorDto.ErrorResponse;
import com.alisa.moviereservationsystem.dto.errorDto.ValidationError;
import com.alisa.moviereservationsystem.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return new ResponseEntity<>(
                new ValidationError(errors),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> bookedHall(BookedHallException e) {
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

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> incorrectPassword(IncorrectPasswordException e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), 400, Instant.now()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> unauthorizedUser(UnauthorizedUserException e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), 403, Instant.now()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> wrongHall(WrongHallException e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), 400, Instant.now()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> duplicateInformation(DuplicateInformationException e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), 409, Instant.now()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> failedPayment(FailedPaymentException e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), 500, Instant.now()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
