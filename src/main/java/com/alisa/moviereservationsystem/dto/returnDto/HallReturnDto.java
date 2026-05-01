package com.alisa.moviereservationsystem.dto.returnDto;

import java.util.List;

public record HallReturnDto(Long id, Integer hallNumber, List<Long> seatIds) {
}
