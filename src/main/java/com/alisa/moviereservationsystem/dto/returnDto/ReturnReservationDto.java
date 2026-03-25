package com.alisa.moviereservationsystem.dto.returnDto;

import java.util.List;

public record ReturnReservationDto(Long id, Float generalPrice, Long userId, List<Long> seatIds, Long showtimeId) {
}
