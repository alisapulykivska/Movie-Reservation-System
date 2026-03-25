package com.alisa.moviereservationsystem.dto.createDto;

import com.alisa.moviereservationsystem.models.enums.SeatStatus;
import com.alisa.moviereservationsystem.models.enums.SeatType;

public record CreateSeatDto(Integer seatNumber, Integer rowNumber, Float price, SeatType type, SeatStatus status, Long hallId) {
}
