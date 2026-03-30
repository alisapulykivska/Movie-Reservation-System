package com.alisa.moviereservationsystem.controllers;

import com.alisa.moviereservationsystem.dto.createDto.CreateReservationDto;
import com.alisa.moviereservationsystem.dto.returnDto.ReturnReservationDto;
import com.alisa.moviereservationsystem.dto.updateDto.UpdateReservationDto;
import com.alisa.moviereservationsystem.models.Reservation;
import com.alisa.moviereservationsystem.models.Seat;
import com.alisa.moviereservationsystem.repositories.CustomUserRepository;
import com.alisa.moviereservationsystem.repositories.SeatRepository;
import com.alisa.moviereservationsystem.services.ReservationService;
import com.alisa.moviereservationsystem.services.SeatService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReturnReservationDto> createReservation(@RequestBody CreateReservationDto reservation) {
        return ResponseEntity.ok(reservationService.createReservation(reservation));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReturnReservationDto> updateReservation(@PathVariable Long id, @RequestBody UpdateReservationDto reservation) {
        return ResponseEntity.ok(reservationService.updateReservation(id, reservation));
    }

    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReturnReservationDto> findReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.findReservationById(id));
    }

    @GetMapping
    public ResponseEntity<List<ReturnReservationDto>> findAllReservations() {
        return ResponseEntity.ok(reservationService.findAllReservations());
    }
}
