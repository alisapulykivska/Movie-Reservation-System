package com.alisa.moviereservationsystem.dto.createDto;

import com.alisa.moviereservationsystem.models.enums.SeatStatus;
import com.alisa.moviereservationsystem.models.enums.SeatType;
import jakarta.validation.constraints.NotNull;

public record SeatCreateDto(
        @NotNull(message = "Seat number must not be null")
        Integer seatNumber,

        @NotNull(message = "Row number must not be null")
        Integer rowNumber,

        @NotNull(message = "Price must not be null")
        Float price,

        @NotNull(message = "Seat type must not be null")
        SeatType type,

        @NotNull(message = "Seat status must not be null")
        SeatStatus status,

        @NotNull(message = "Hall id must not be null")
        Long hallId) {
}
