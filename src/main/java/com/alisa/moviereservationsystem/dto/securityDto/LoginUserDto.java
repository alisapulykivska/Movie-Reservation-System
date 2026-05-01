package com.alisa.moviereservationsystem.dto.securityDto;

import jakarta.validation.constraints.*;

public record LoginUserDto(
        @NotBlank(message = "Username must not be blank")
        @Size(max = 20, message = "Username must not exceed 20 characters")
        String username,

        @NotBlank(message = "Email must not be blank")
        String email,

        @NotBlank(message = "Password must not be blank")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password) {
}
