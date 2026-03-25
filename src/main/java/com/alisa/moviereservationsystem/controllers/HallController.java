package com.alisa.moviereservationsystem.controllers;

import com.alisa.moviereservationsystem.dto.createDto.CreateHallDto;
import com.alisa.moviereservationsystem.dto.returnDto.ReturnHallDto;
import com.alisa.moviereservationsystem.dto.updateDto.UpdateHallDto;
import com.alisa.moviereservationsystem.models.Hall;
import com.alisa.moviereservationsystem.models.Seat;
import com.alisa.moviereservationsystem.repositories.SeatRepository;
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
    public ResponseEntity<ReturnHallDto> createHall(@RequestBody CreateHallDto hall) {
        return ResponseEntity.ok(hallService.createHall(hall));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReturnHallDto> updateHall(@PathVariable Long id, @RequestBody UpdateHallDto hall) {
        return ResponseEntity.ok(hallService.updateHall(id, hall));
    }

    @DeleteMapping("/{id}")
    public void deleteHall(@PathVariable Long id) {
        hallService.deleteHall(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReturnHallDto> findHallById(@PathVariable Long id) {
        return ResponseEntity.ok(hallService.findHallById(id));
    }

    @GetMapping
    public ResponseEntity<List<ReturnHallDto>> findAllHalls() {
        return ResponseEntity.ok(hallService.findAllHalls());
    }
}
