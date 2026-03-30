package com.alisa.moviereservationsystem.controllers;

import com.alisa.moviereservationsystem.dto.createDto.CreateMovieDto;
import com.alisa.moviereservationsystem.dto.returnDto.ReturnMovieDto;
import com.alisa.moviereservationsystem.dto.updateDto.UpdateMovieDto;
import com.alisa.moviereservationsystem.models.Movie;
import com.alisa.moviereservationsystem.services.MovieService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/movie")
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    public ResponseEntity<ReturnMovieDto> createMovie(@RequestBody CreateMovieDto movie) {
        return ResponseEntity.ok(movieService.createMovie(movie));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReturnMovieDto> updateMovie(@PathVariable Long id, @RequestBody UpdateMovieDto movie) {
        return ResponseEntity.ok(movieService.updateMovie(id, movie));
    }

    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReturnMovieDto> findMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.findMovieById(id));
    }

    @GetMapping
    public ResponseEntity<List<ReturnMovieDto>> findAllMovies() {
        return ResponseEntity.ok(movieService.findAllMovies());
    }
}
