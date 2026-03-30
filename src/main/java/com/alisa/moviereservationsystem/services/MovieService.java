package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.dto.createDto.CreateMovieDto;
import com.alisa.moviereservationsystem.dto.returnDto.ReturnMovieDto;
import com.alisa.moviereservationsystem.dto.updateDto.UpdateMovieDto;
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

    public ReturnMovieDto createMovie(CreateMovieDto movie) {
        Movie newMovie = new Movie();
        newMovie.setTitle(movie.title());
        newMovie.setDescription(movie.description());
        newMovie.setGenres(movie.genres());
        Movie savedMovie = movieRepository.save(newMovie);
        return new ReturnMovieDto(
                savedMovie.getId(),
                savedMovie.getTitle(),
                savedMovie.getDescription(),
                savedMovie.getPosterUrl(),
                savedMovie.getGenres()
        );
    }

    public ReturnMovieDto updateMovie(Long id, UpdateMovieDto movie) {
        Movie oldMovie = movieRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        if(movie == null) {
            throw new IllegalArgumentException("Movie information is null");
        } else {
            if(movie.title() != null && !movie.title().isEmpty()) {
                oldMovie.setTitle(movie.title());
            }
            if(movie.description() != null && !movie.description().isEmpty()) {
                oldMovie.setDescription(movie.description());
            }
            if(movie.posterUrl() != null && !movie.posterUrl().isEmpty()) {
                oldMovie.setPosterUrl(movie.posterUrl());
            }
            if(movie.genres() != null && !movie.genres().isEmpty()) {
                oldMovie.setGenres(movie.genres());
            }
        }

        Movie savedMovie = movieRepository.save(oldMovie);
        return new ReturnMovieDto(
                savedMovie.getId(),
                savedMovie.getTitle(),
                savedMovie.getDescription(),
                savedMovie.getPosterUrl(),
                savedMovie.getGenres()
        );
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    public ReturnMovieDto findMovieById(Long id) {
        Movie movie = movieRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return new ReturnMovieDto(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getPosterUrl(),
                movie.getGenres()
        );
    }

    public List<ReturnMovieDto> findAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(
                movie -> new ReturnMovieDto(
                        movie.getId(),
                        movie.getTitle(),
                        movie.getDescription(),
                        movie.getPosterUrl(),
                        movie.getGenres()
                )).toList();
    }
}
