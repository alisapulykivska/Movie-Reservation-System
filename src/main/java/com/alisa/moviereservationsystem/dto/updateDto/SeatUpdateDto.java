package com.alisa.moviereservationsystem.dto.updateDto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SeatUpdateDto(
        @NotNull(message = "Seat number must not be null")
        Integer seatNumber,

        @NotNull(message = "Row number must not be null")
        Integer rowNumber,

        @NotNull(message = "Price must not be null")
        BigDecimal price) {
}
