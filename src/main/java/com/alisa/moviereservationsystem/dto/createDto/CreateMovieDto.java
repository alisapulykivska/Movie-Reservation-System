package com.alisa.moviereservationsystem.dto.createDto;

import com.alisa.moviereservationsystem.models.enums.MovieGenre;

import java.util.List;

public record CreateMovieDto(String title, String description, String posterUrl, List<MovieGenre> genres) {
}
