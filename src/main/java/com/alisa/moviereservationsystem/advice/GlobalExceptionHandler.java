package com.alisa.moviereservationsystem.advice;

import com.alisa.moviereservationsystem.dto.errorDto.ErrorResponse;
import com.alisa.moviereservationsystem.dto.errorDto.ValidationError;
import com.alisa.moviereservationsystem.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        log.warn("Validation failed: {}", errors);

        return new ResponseEntity<>(
                new ValidationError(errors),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> bookedHall(BookedHallException e) {

        log.warn("Booked Hall: {}", e.getMessage());

        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), 400, Instant.now()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> invalidReservationStatus(InvalidReservationStatusException e) {

        log.warn("Invalid reservation status: {}", e.getMessage());

        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), 400, Instant.now()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> pastShowtime(PastShowtimeException e) {

        log.warn("Past showtime: {}", e.getMessage());

        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), 400, Instant.now()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> informationNotFound(InformationNotFoundException e) {

        log.warn("Information not found: {}", e.getMessage());

        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), 404, Instant.now()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> seatsUnavailable(SeatsUnavailableException e) {

        log.warn("SeatsUnavailable: {}", e.getMessage());

        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), 400, Instant.now()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> incorrectPassword(IncorrectPasswordException e) {

        log.warn("Incorrect password: {}", e.getMessage());

        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), 400, Instant.now()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> unauthorizedUser(UnauthorizedUserException e) {

        log.warn("Unauthorized user: {}", e.getMessage());

        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), 403, Instant.now()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> wrongHall(WrongHallException e) {

        log.warn("Wrong hall: {}", e.getMessage());

        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), 400, Instant.now()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> duplicateInformation(DuplicateInformationException e) {

        log.warn("Duplicate information: {}", e.getMessage());

        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), 409, Instant.now()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> failedPayment(FailedPaymentException e) {

        log.error("Failed payment: {}", e.getMessage());

        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), 500, Instant.now()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
