package com.alisa.moviereservationsystem.dto.returnDto;

import com.alisa.moviereservationsystem.models.enums.UserRole;

public record CustomUserReturnDto(Long id, String username, String email, UserRole userRole) {
}
