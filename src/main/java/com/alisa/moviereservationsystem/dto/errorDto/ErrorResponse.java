package com.alisa.moviereservationsystem.dto.errorDto;

import java.time.Instant;

public record ErrorResponse(String message, int status, Instant timestamp) {
}
