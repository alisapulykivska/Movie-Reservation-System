package com.alisa.moviereservationsystem.dto.updateDto;

import com.alisa.moviereservationsystem.models.enums.ShowtimeType;

import java.time.OffsetDateTime;

public record ShowtimeUpdateDto(OffsetDateTime dateTime, ShowtimeType showtimeType, Long hallId) {
}
