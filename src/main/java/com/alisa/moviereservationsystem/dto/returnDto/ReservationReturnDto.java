package com.alisa.moviereservationsystem.dto.returnDto;

import com.alisa.moviereservationsystem.models.enums.ReservationStatus;

import java.time.Instant;
import java.util.List;

public record ReservationReturnDto(Long id, Float generalPrice, ReservationStatus status, Instant timeStamp, Long userId, List<Long> seatIds, Long showtimeId) {
}
