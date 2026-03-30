package com.alisa.moviereservationsystem.dto.updateDto;

import java.util.List;

public record UpdateHallDto(Integer hallNumber, List<Long> seatIds) {
}
