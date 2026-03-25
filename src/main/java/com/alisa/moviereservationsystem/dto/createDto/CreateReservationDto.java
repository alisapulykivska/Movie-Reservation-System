package com.alisa.moviereservationsystem.dto.createDto;

import java.util.List;

public record CreateReservationDto(Float generalPrice, Long userId, List<Long> seatIds, Long showtimeId) {
}
