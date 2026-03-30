package com.alisa.moviereservationsystem.dto.updateDto;

import com.alisa.moviereservationsystem.models.enums.MovieGenre;

import java.util.List;

public record UpdateMovieDto(String title, String description, String posterUrl, List<MovieGenre> genres) {
}
