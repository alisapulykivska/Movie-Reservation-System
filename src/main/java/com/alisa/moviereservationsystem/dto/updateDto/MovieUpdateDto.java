package com.alisa.moviereservationsystem.dto.updateDto;

import com.alisa.moviereservationsystem.models.enums.MovieGenre;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record MovieUpdateDto(
        @NotBlank(message = "Movie title must not be blank")
        String title,

        @NotBlank(message = "Description must not be blank")
        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @NotBlank(message = "Poster Url must not be blank")
        String posterUrl,

        @NotNull(message = "Genres list must not be null")
        @Size(min = 1, message = "Genres list must contain at least 1 element")
        List<@NotNull(message = "Each genre must not be null") MovieGenre> genres) {
}
