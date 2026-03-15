package com.alisa.moviereservationsystem.controllers;

import com.alisa.moviereservationsystem.models.Hall;
import com.alisa.moviereservationsystem.services.HallService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/hall")
public class HallController {

    private final HallService hallService;

    @PostMapping
    public ResponseEntity<Hall> createHall(@RequestBody Hall hall) {
        return ResponseEntity.ok(hallService.createHall(hall));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hall> updateHall(@PathVariable Long id, @RequestBody Hall hall) {
        return ResponseEntity.ok(hallService.updateHall(id, hall));
    }

    @DeleteMapping("/{id}")
    public void deleteHall(@PathVariable Long id) {
        hallService.deleteHall(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hall> findHallById(@PathVariable Long id) {
        return ResponseEntity.ok(hallService.findHallById(id));
    }

    @GetMapping
    public ResponseEntity<List<Hall>> findAllHalls() {
        return ResponseEntity.ok(hallService.findAllHalls());
    }
}
