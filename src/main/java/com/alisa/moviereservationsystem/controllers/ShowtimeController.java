package com.alisa.moviereservationsystem.controllers;

import com.alisa.moviereservationsystem.dto.createDto.CreateShowtimeDto;
import com.alisa.moviereservationsystem.dto.returnDto.ReturnShowtimeDto;
import com.alisa.moviereservationsystem.dto.updateDto.UpdateShowtimeDto;
import com.alisa.moviereservationsystem.models.Showtime;
import com.alisa.moviereservationsystem.services.ShowtimeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/showtime")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @PostMapping
    public ResponseEntity<ReturnShowtimeDto> createShowtime(@RequestBody CreateShowtimeDto showtime) {
        return ResponseEntity.ok(showtimeService.createShowtime(showtime));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReturnShowtimeDto> updateShowtime(@PathVariable Long id, @RequestBody UpdateShowtimeDto showtime) {
        return ResponseEntity.ok(showtimeService.updateShowtime(id, showtime));
    }

    @DeleteMapping("/{id}")
    public void deleteShowtime(@PathVariable Long id) {
        showtimeService.deleteShowtime(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReturnShowtimeDto> findShowtimeById(@PathVariable Long id) {
        return ResponseEntity.ok(showtimeService.findShowtimeById(id));
    }

    @GetMapping
    public ResponseEntity<List<ReturnShowtimeDto>> findAllShowtimes() {
        return ResponseEntity.ok(showtimeService.findAllShowtimes());
    }

    @GetMapping("/{movieId}/movie")
    public ResponseEntity<List<ReturnShowtimeDto>> retrieveAllShowtimesByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(showtimeService.getAllShowtimesForMovie(movieId));
    }
}
