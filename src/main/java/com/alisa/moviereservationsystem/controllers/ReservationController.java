package com.alisa.moviereservationsystem.controllers;

import com.alisa.moviereservationsystem.dto.createDto.ReservationCreateDto;
import com.alisa.moviereservationsystem.dto.returnDto.ReservationReturnDto;
import com.alisa.moviereservationsystem.dto.returnDto.UpdateReservationReturnDto;
import com.alisa.moviereservationsystem.dto.updateDto.ReservationUpdateDto;
import com.alisa.moviereservationsystem.exceptions.FailedPaymentException;
import com.alisa.moviereservationsystem.services.ReservationService;
import jakarta.validation.Valid;
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
    public ResponseEntity<ReservationReturnDto> createReservation(@Valid @RequestBody ReservationCreateDto reservation) {
        return ResponseEntity.ok(reservationService.createReservation(reservation));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UpdateReservationReturnDto> updateReservation(@PathVariable Long id, @Valid @RequestBody ReservationUpdateDto reservation) {
        return ResponseEntity.ok(reservationService.updateReservation(id, reservation));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationReturnDto> findReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.findReservationById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ReservationReturnDto>> findAllReservations() {
        return ResponseEntity.ok(reservationService.findAllReservations());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationReturnDto>> findAllReservationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.findAllReservationsForUser(userId));
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<ReservationReturnDto> confirmReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.confirmReservation(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ReservationReturnDto> cancelReservation(@PathVariable Long id) throws FailedPaymentException {
        return ResponseEntity.ok(reservationService.cancelReservation(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/revenue")
    public ResponseEntity<Float> getTotalRevenue() {
        return ResponseEntity.ok(reservationService.getTotalRevenue());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<Long> getReservationCountForMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(reservationService.getReservationCountForMovie(movieId));
    }

    @GetMapping("/revenue/{showtimeId}")
    public ResponseEntity<Float> getRevenueForShowtime(@PathVariable Long showtimeId) {
        return ResponseEntity.ok(reservationService.getRevenueForShowtime(showtimeId));
    }

    @PatchMapping("/charge/{reservationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservationReturnDto> chargeExtraPayment(@PathVariable Long reservationId, @RequestParam Float priceDifference) throws FailedPaymentException {
        return ResponseEntity.ok(reservationService.chargeExtraPayment(reservationId, priceDifference));
    }

    @PatchMapping("/refund/{reservationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReservationReturnDto> refundPriceDifference(@PathVariable Long reservationId, @RequestParam Float priceDifference) throws FailedPaymentException {
        return ResponseEntity.ok(reservationService.refundPriceDifference(reservationId, priceDifference));
    }
}
