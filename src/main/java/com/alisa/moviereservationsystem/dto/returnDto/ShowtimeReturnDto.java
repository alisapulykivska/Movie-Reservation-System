package com.alisa.moviereservationsystem.dto.returnDto;

import com.alisa.moviereservationsystem.models.enums.ShowtimeStatus;
import com.alisa.moviereservationsystem.models.enums.ShowtimeType;

import java.time.OffsetDateTime;

public record ShowtimeReturnDto(Long id, OffsetDateTime dateTime, ShowtimeType showtimeType, ShowtimeStatus status, Long movieId, Long hallId) {
}
