package com.alisa.moviereservationsystem.dto.returnDto;

import java.util.List;

public record ReturnHallDto(Long id, Integer hallNumber, List<Long> seatIds) {
}
