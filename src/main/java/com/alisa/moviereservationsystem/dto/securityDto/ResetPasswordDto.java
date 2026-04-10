package com.alisa.moviereservationsystem.dto.securityDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResetPasswordDto(
        @NotNull(message = "New password field must not be blank")
        String newPassword,

        @NotBlank(message = "Confirm password field must not be blank")
        String confirmPassword) {
}
