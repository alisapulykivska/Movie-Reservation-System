package com.alisa.moviereservationsystem.dto.errorDto;

import java.util.Map;

public record ValidationError(Map<String, String> errors) {
}
