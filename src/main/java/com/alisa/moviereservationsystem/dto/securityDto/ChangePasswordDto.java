package com.alisa.moviereservationsystem.dto.securityDto;

import com.alisa.moviereservationsystem.repositories.CustomUserRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangePasswordDto(
        @NotBlank(message = "Old password field must not be blank")
        String oldPassword,

        @NotBlank(message = "New password field must not be blank")
        @Size(min = 8, message = "New password must be at least 8 characters long")
        String newPassword) {
}
