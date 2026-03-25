package com.alisa.moviereservationsystem.dto.updateDto;

import com.alisa.moviereservationsystem.models.enums.ShowtimeType;

import java.time.OffsetDateTime;

public record UpdateShowtimeDto(OffsetDateTime dateTime, ShowtimeType showtimeType) {
}
