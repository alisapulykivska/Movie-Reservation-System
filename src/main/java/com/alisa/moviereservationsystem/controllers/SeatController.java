package com.alisa.moviereservationsystem.controllers;

import com.alisa.moviereservationsystem.dto.createDto.CreateSeatDto;
import com.alisa.moviereservationsystem.dto.returnDto.ReturnSeatDto;
import com.alisa.moviereservationsystem.dto.updateDto.UpdateSeatDto;
import com.alisa.moviereservationsystem.models.Seat;
import com.alisa.moviereservationsystem.services.SeatService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/seat")
public class SeatController {

    private final SeatService seatService;

    @PostMapping
    public ResponseEntity<ReturnSeatDto> createSeat(@RequestBody CreateSeatDto seat) {
        return ResponseEntity.ok(seatService.createSeat(seat));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReturnSeatDto> updateSeat(@PathVariable Long id, @RequestBody UpdateSeatDto seat) {
        return ResponseEntity.ok(seatService.updateSeat(id, seat));
    }

    @DeleteMapping("/{id}")
    public void deleteSeat(@PathVariable Long id) {
        seatService.deleteSeat(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReturnSeatDto> findSeatById(@PathVariable Long id) {
        return ResponseEntity.ok(seatService.findSeatById(id));
    }

    @GetMapping
    public ResponseEntity<List<ReturnSeatDto>> findAllSeats() {
        return ResponseEntity.ok(seatService.findAllSeats());
    }

    @GetMapping("/{showtimeId}/showtime")
    public ResponseEntity<List<ReturnSeatDto>> retrieveAllSeatsByShowtime(@PathVariable Long showtimeId) {
        return ResponseEntity.ok(seatService.getAllSeatsForShowtime(showtimeId));
    }
}
