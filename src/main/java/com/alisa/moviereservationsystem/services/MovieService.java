package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.models.Movie;
import com.alisa.moviereservationsystem.repositories.MovieRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public Movie updateMovie(Long id, Movie movie) {
        Movie oldMovie = movieRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        if(movie == null) {
            throw new IllegalArgumentException("Movie information is null");
        } else {
            if(movie.getTitle() != null && !movie.getTitle().isEmpty()) {
                oldMovie.setTitle(movie.getTitle());
            }
            if(movie.getDescription() != null && !movie.getDescription().isEmpty()) {
                oldMovie.setDescription(movie.getDescription());
            }
            if(movie.getPosterUrl() != null && !movie.getPosterUrl().isEmpty()) {
                oldMovie.setPosterUrl(movie.getPosterUrl());
            }
            if(movie.getGenres() != null && !movie.getGenres().isEmpty()) {
                oldMovie.setGenres(movie.getGenres());
            }
        }
        return movieRepository.save(oldMovie);
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    public Movie findMovieById(Long id) {
        return movieRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<Movie> findAllMovies() {
        return movieRepository.findAll();
    }
}
