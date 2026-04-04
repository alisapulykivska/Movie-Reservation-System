package com.alisa.moviereservationsystem.dto.createDto;

import com.alisa.moviereservationsystem.models.enums.ShowtimeType;

import java.time.OffsetDateTime;

public record ShowtimeCreateDto(OffsetDateTime dateTime, ShowtimeType showtimeType, Long movieId, Long hallId) {
}
