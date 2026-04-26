package com.alisa.moviereservationsystem.controllers;

import com.alisa.moviereservationsystem.dto.returnDto.MovieReturnDto;
import com.alisa.moviereservationsystem.models.enums.MovieGenre;
import com.alisa.moviereservationsystem.services.MovieService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@AllArgsConstructor
public class MovieGraphQLController {

    private final MovieService movieService;

    @QueryMapping
    public List<MovieReturnDto> searchMovies(@Argument String title) {
        if (title == null || title.isEmpty()) {
            return movieService.findAllMovies();
        }
        return movieService.findMoviesByTitle(title);
    }

    @QueryMapping
    public List<MovieReturnDto> moviesByGenres(@Argument List<String> genres) {
        List<MovieGenre> movieGenres = genres.stream()
                .map(MovieGenre::valueOf)
                .toList();
        return movieService.findAllMoviesByGenres(movieGenres);
    }

    @QueryMapping
    public MovieReturnDto movieById(@Argument Long id) {
        return movieService.findMovieById(id);
    }

    @QueryMapping
    public List<MovieReturnDto> allMovies() {
        return movieService.findAllMovies();
    }
}
