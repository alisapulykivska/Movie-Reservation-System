package com.alisa.moviereservationsystem.dto.updateDto;

import com.alisa.moviereservationsystem.models.enums.ShowtimeType;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record ShowtimeUpdateDto(
        @NotNull(message = "Showtime date and time must not be null")
        OffsetDateTime dateTime,

        @NotNull(message = "Showtime type must not be null")
        ShowtimeType showtimeType,

        @NotNull(message = "Hall id must not be null")
        Long hallId) {
}
