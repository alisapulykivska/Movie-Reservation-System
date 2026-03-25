package com.alisa.moviereservationsystem.dto.createDto;

import java.util.List;

public record CreateHallDto(Integer hallNumber, List<Long> seatIds) {
}
