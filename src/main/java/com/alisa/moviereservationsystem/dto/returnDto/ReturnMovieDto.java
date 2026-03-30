package com.alisa.moviereservationsystem.dto.returnDto;

import com.alisa.moviereservationsystem.models.enums.MovieGenre;

import java.util.List;

public record ReturnMovieDto(Long id, String title, String description, String posterUrl, List<MovieGenre> genres) {
}
