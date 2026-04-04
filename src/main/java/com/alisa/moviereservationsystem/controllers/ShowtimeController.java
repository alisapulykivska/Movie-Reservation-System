package com.alisa.moviereservationsystem.controllers;

import com.alisa.moviereservationsystem.dto.createDto.ShowtimeCreateDto;
import com.alisa.moviereservationsystem.dto.reportDto.CapacityReturnDto;
import com.alisa.moviereservationsystem.dto.returnDto.ShowtimeReturnDto;
import com.alisa.moviereservationsystem.dto.updateDto.ShowtimeUpdateDto;
import com.alisa.moviereservationsystem.services.ShowtimeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/showtime")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @PreAuthorize("('ADMIN')")
    @PostMapping
    public ResponseEntity<ShowtimeReturnDto> createShowtime(@RequestBody ShowtimeCreateDto showtime) {
        return ResponseEntity.ok(showtimeService.createShowtime(showtime));
    }

    @PreAuthorize("('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ShowtimeReturnDto> updateShowtime(@PathVariable Long id, @RequestBody ShowtimeUpdateDto showtime) {
        return ResponseEntity.ok(showtimeService.updateShowtime(id, showtime));
    }

    @PreAuthorize("('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteShowtime(@PathVariable Long id) {
        showtimeService.deleteShowtime(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowtimeReturnDto> findShowtimeById(@PathVariable Long id) {
        return ResponseEntity.ok(showtimeService.findShowtimeById(id));
    }

    @PreAuthorize("('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ShowtimeReturnDto>> findAllShowtimes() {
        return ResponseEntity.ok(showtimeService.findAllShowtimes());
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ShowtimeReturnDto>> retrieveAllShowtimesByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(showtimeService.getAllShowtimesForMovie(movieId));
    }

    @GetMapping("/date")
    public ResponseEntity<List<ShowtimeReturnDto>> retrieveAllShowtimesByDate(@RequestParam LocalDate date) {
        return ResponseEntity.ok(showtimeService.getShowtimeForDate(date));
    }
}
