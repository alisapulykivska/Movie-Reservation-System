package com.alisa.moviereservationsystem.dto.createDto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record HallCreateDto(
        @NotNull(message = "Hall number must not be null")
        Integer hallNumber,

        @NotNull(message = "The seat ids list must not be null")
        @Size(min = 1, message = "Seat ids list must contain at least 1 element")
        List<@NotNull(message = "Each seat id must not be null") Long> seatIds) {
}
