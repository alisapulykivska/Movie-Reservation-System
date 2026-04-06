package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.dto.createDto.MovieCreateDto;
import com.alisa.moviereservationsystem.dto.returnDto.MovieReturnDto;
import com.alisa.moviereservationsystem.dto.updateDto.MovieUpdateDto;
import com.alisa.moviereservationsystem.exceptions.InformationIsNullException;
import com.alisa.moviereservationsystem.exceptions.InformationNotFoundException;
import com.alisa.moviereservationsystem.models.Movie;
import com.alisa.moviereservationsystem.models.enums.MovieGenre;
import com.alisa.moviereservationsystem.models.enums.ShowtimeStatus;
import com.alisa.moviereservationsystem.repositories.MovieRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieReturnDto createMovie(MovieCreateDto movie) {
        Movie newMovie = new Movie();
        newMovie.setTitle(movie.title());
        newMovie.setDescription(movie.description());
        newMovie.setGenres(movie.genres());
        newMovie.setPosterUrl(movie.posterUrl());
        Movie savedMovie = movieRepository.save(newMovie);
        return toReturnDto(savedMovie);
    }

    public MovieReturnDto updateMovie(Long movieId, MovieUpdateDto movie) {
        Movie oldMovie = movieRepository.findById(movieId)
                .orElseThrow(() -> new InformationNotFoundException("Movie", movieId));
        if(movie == null) {
            throw new InformationIsNullException("Movie information is null");
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
        return toReturnDto(savedMovie);
    }

    public void deleteMovie(Long movieId) {
        movieRepository.deleteById(movieId);
    }

    public MovieReturnDto findMovieById(Long id) {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new
                InformationNotFoundException("Movie", id));
        return toReturnDto(movie);
    }

    public List<MovieReturnDto> findAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(this::toReturnDto)
                .toList();
    }

    public List<MovieReturnDto> findAllMoviesByGenres(List<MovieGenre> genres) {
        return movieRepository.findAll().stream()
                .filter(movie -> movie.getGenres()
                        .stream().anyMatch(genres::contains))
                .filter(movie -> movie.getShowtimes().stream()
                        .anyMatch(showtime -> showtime.getStatus() == ShowtimeStatus.Upcoming))
                .map(this::toReturnDto)
                .toList();
    }

    public List<MovieReturnDto> findMostPopularMovies() {
        return movieRepository.findAllSortedByReservationCountDesc().stream()
                .filter(movie -> movie.getShowtimes().stream()
                        .anyMatch(showtime -> showtime.getStatus() == ShowtimeStatus.Upcoming))
                .map(this::toReturnDto)
                .toList();
    }

    public List<MovieReturnDto> findMoviesByTitle(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::toReturnDto)
                .toList();
    }

    private MovieReturnDto toReturnDto(Movie movie) {
        return new MovieReturnDto(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getPosterUrl(),
                movie.getGenres()
        );
    }
}
