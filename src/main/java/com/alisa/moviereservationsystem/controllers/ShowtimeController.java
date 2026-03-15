package com.alisa.moviereservationsystem.controllers;

import com.alisa.moviereservationsystem.models.Showtime;
import com.alisa.moviereservationsystem.services.ShowtimeService;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/showtime")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @PostMapping
    public ResponseEntity<Showtime> createShowtime(@RequestBody Showtime showtime) {
        return ResponseEntity.ok(showtimeService.createShowtime(showtime));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Showtime> updateShowtime(@PathVariable Long id, @RequestBody Showtime showtime) {
        return ResponseEntity.ok(showtimeService.updateShowtime(id, showtime));
    }

    @DeleteMapping("/{id}")
    public void deleteShowtime(@PathVariable Long id) {
        showtimeService.deleteShowtime(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Showtime> findShowtimeById(@PathVariable Long id) {
        return ResponseEntity.ok(showtimeService.findShowtimeById(id));
    }

    @GetMapping
    public ResponseEntity<List<Showtime>> findAllShowtimes() {
        return ResponseEntity.ok(showtimeService.findAllShowtimes());
    }
}
