package com.alisa.moviereservationsystem.dto.returnDto;

import com.alisa.moviereservationsystem.models.enums.ReservationStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record UpdateReservationReturnDto(Long id, BigDecimal newPrice, BigDecimal oldPrice, BigDecimal priceDifference, ReservationStatus status, Instant timeStamp, Long userId, List<Long> seatIds, Long showtimeId) {
}
