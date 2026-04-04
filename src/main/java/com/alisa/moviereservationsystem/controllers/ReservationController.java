package com.alisa.moviereservationsystem.controllers;

import com.alisa.moviereservationsystem.dto.createDto.ReservationCreateDto;
import com.alisa.moviereservationsystem.dto.returnDto.ReservationReturnDto;
import com.alisa.moviereservationsystem.dto.updateDto.ReservationUpdateDto;
import com.alisa.moviereservationsystem.services.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<ReservationReturnDto> createReservation(@RequestBody ReservationCreateDto reservation) {
        return ResponseEntity.ok(reservationService.createReservation(reservation));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ReservationReturnDto> updateReservation(@PathVariable Long id, @RequestBody ReservationUpdateDto reservation) {
        return ResponseEntity.ok(reservationService.updateReservation(id, reservation));
    }

    @PreAuthorize("('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationReturnDto> findReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.findReservationById(id));
    }

    @PreAuthorize("('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ReservationReturnDto>> findAllReservations() {
        return ResponseEntity.ok(reservationService.findAllReservations());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationReturnDto>> retrieveAllReservationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getAllReservationsForUser(userId));
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<ReservationReturnDto> confirmReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.confirmReservation(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ReservationReturnDto> cancelReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.cancelReservation(id));
    }

    @PreAuthorize("('ADMIN')")
    @GetMapping("/revenue")
    public ResponseEntity<Float> getTotalRevenue() {
        return ResponseEntity.ok(reservationService.getTotalRevenue());
    }

    @PreAuthorize("('ADMIN')")
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<Long> getReservationCountForMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(reservationService.getReservationCountForMovie(movieId));
    }
}
