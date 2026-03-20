package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.models.Reservation;
import com.alisa.moviereservationsystem.repositories.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public Reservation createReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Reservation updateReservation(Long id, Reservation reservation) {
        Reservation oldReservation = reservationRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        if(reservation == null) {
            throw new IllegalArgumentException("Reservation information is null");
        } else {
            if(reservation.getDateTime() != null) {
                oldReservation.setDateTime(reservation.getDateTime());
            }
            if(reservation.getGeneralPrice() != null) {
                oldReservation.setGeneralPrice(reservation.getGeneralPrice());
            }
        }
        return reservationRepository.save(oldReservation);
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public Reservation findReservationById(Long id) {
        return reservationRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<Reservation> findAllReservations() {
        return reservationRepository.findAll();
    }

}
