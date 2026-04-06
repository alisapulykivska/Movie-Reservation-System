package com.alisa.moviereservationsystem.dto.securityDto;

import com.alisa.moviereservationsystem.repositories.CustomUserRepository;

public record ChangePasswordDto(String oldPassword, String newPassword) {
}
