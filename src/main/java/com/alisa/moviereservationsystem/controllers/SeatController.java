package com.alisa.moviereservationsystem.controllers;

import com.alisa.moviereservationsystem.dto.createDto.SeatCreateDto;
import com.alisa.moviereservationsystem.dto.returnDto.SeatReturnDto;
import com.alisa.moviereservationsystem.dto.updateDto.SeatUpdateDto;
import com.alisa.moviereservationsystem.services.SeatService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/seat")
public class SeatController {

    private final SeatService seatService;

    @PreAuthorize("('ADMIN')")
    @PostMapping
    public ResponseEntity<SeatReturnDto> createSeat(@RequestBody SeatCreateDto seat) {
        return ResponseEntity.ok(seatService.createSeat(seat));
    }

    @PreAuthorize("('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SeatReturnDto> updateSeat(@PathVariable Long id, @RequestBody SeatUpdateDto seat) {
        return ResponseEntity.ok(seatService.updateSeat(id, seat));
    }

    @PreAuthorize("('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteSeat(@PathVariable Long id) {
        seatService.deleteSeat(id);
    }

    @PreAuthorize("('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<SeatReturnDto> findSeatById(@PathVariable Long id) {
        return ResponseEntity.ok(seatService.findSeatById(id));
    }

    @PreAuthorize("('ADMIN')")
    @GetMapping
    public ResponseEntity<List<SeatReturnDto>> findAllSeats() {
        return ResponseEntity.ok(seatService.findAllSeats());
    }

    @GetMapping("/showtime/{showtimeId}")
    public ResponseEntity<List<SeatReturnDto>> retrieveAllSeatsByShowtime(@PathVariable Long showtimeId) {
        return ResponseEntity.ok(seatService.getAllSeatsForShowtime(showtimeId));
    }
}
