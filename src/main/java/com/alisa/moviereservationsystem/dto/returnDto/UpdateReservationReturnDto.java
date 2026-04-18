package com.alisa.moviereservationsystem.dto.returnDto;

import com.alisa.moviereservationsystem.models.enums.ReservationStatus;

import java.time.Instant;
import java.util.List;

public record UpdateReservationReturnDto(Long id, Float newPrice, Float oldPrice, Float priceDifference, ReservationStatus status, Instant timeStamp, Long userId, List<Long> seatIds, Long showtimeId) {
}
