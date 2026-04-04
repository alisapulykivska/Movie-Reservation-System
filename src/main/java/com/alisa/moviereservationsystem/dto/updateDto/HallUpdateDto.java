package com.alisa.moviereservationsystem.dto.updateDto;

import java.util.List;

public record HallUpdateDto(Integer hallNumber, List<Long> seatIds) {
}
