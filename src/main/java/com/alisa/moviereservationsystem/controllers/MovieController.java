package com.alisa.moviereservationsystem.controllers;

import com.alisa.moviereservationsystem.dto.createDto.MovieCreateDto;
import com.alisa.moviereservationsystem.dto.returnDto.MovieReturnDto;
import com.alisa.moviereservationsystem.dto.updateDto.MovieUpdateDto;
import com.alisa.moviereservationsystem.models.enums.MovieGenre;
import com.alisa.moviereservationsystem.services.MovieService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/movie")
public class MovieController {

    private final MovieService movieService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MovieReturnDto> createMovie(@Valid @RequestBody MovieCreateDto movie) {
        return ResponseEntity.ok(movieService.createMovie(movie));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MovieReturnDto> updateMovie(@PathVariable Long id, @Valid @RequestBody MovieUpdateDto movie) {
        return ResponseEntity.ok(movieService.updateMovie(id, movie));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieReturnDto> findMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.findMovieById(id));
    }

    @GetMapping
    public ResponseEntity<List<MovieReturnDto>> findAllMovies() {
        return ResponseEntity.ok(movieService.findAllMovies());
    }

    @GetMapping("/genres")
    public ResponseEntity<List<MovieReturnDto>> findMoviesByGenres(@RequestParam List<MovieGenre> genres) {
        return ResponseEntity.ok(movieService.findAllMoviesByGenres(genres));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<MovieReturnDto>> findMostPopularMovies() {
        return ResponseEntity.ok(movieService.findMostPopularMovies());
    }

    @GetMapping("/search")
    public ResponseEntity<List<MovieReturnDto>> findMoviesByTitle(@RequestParam String title) {
        return ResponseEntity.ok(movieService.findMoviesByTitle(title));
    }
}
