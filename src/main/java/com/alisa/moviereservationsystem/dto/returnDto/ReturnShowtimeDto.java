package com.alisa.moviereservationsystem.dto.returnDto;

import com.alisa.moviereservationsystem.models.enums.ShowtimeType;

import java.time.OffsetDateTime;

public record ReturnShowtimeDto(Long id, OffsetDateTime dateTime, ShowtimeType showtimeType, Long movieId) {
}
