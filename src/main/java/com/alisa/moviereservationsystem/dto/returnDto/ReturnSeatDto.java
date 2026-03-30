package com.alisa.moviereservationsystem.dto.returnDto;

import com.alisa.moviereservationsystem.models.enums.SeatStatus;
import com.alisa.moviereservationsystem.models.enums.SeatType;

public record ReturnSeatDto(Long id, Integer seatNumber, Integer rowNumber, Float price, SeatType type, SeatStatus status, Long hallId) {
}
