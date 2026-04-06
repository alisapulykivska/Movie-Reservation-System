package com.alisa.moviereservationsystem.dto.createDto;

import java.util.List;

public record HallCreateDto(Integer hallNumber, List<Long> seatIds) {
}
