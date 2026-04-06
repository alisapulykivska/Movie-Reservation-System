package com.alisa.moviereservationsystem.dto.errorDto;

import java.time.Instant;
import java.time.LocalDateTime;

public record ErrorResponse(String message, int status, Instant timestamp) {
}
