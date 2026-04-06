package com.alisa.moviereservationsystem.dto.createDto;

import java.util.List;

public record ReservationCreateDto(Long userId, List<Long> seatIds, Long showtimeId) {
}
