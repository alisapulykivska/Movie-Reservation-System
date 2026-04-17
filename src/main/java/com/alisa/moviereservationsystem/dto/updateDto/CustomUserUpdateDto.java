package com.alisa.moviereservationsystem.dto.updateDto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CustomUserUpdateDto(
        @NotBlank(message = "Username must not be blank")
        @Size(max = 20, message = "Username must not exceed 20 characters")
        String username,

        @NotBlank(message = "Email must not be blank")
        String email) {
}
