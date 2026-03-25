package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.dto.createDto.CreateReservationDto;
import com.alisa.moviereservationsystem.dto.returnDto.ReturnReservationDto;
import com.alisa.moviereservationsystem.dto.updateDto.UpdateReservationDto;
import com.alisa.moviereservationsystem.models.CustomUser;
import com.alisa.moviereservationsystem.models.Reservation;
import com.alisa.moviereservationsystem.models.Seat;
import com.alisa.moviereservationsystem.models.Showtime;
import com.alisa.moviereservationsystem.repositories.CustomUserRepository;
import com.alisa.moviereservationsystem.repositories.ReservationRepository;
import com.alisa.moviereservationsystem.repositories.SeatRepository;
import com.alisa.moviereservationsystem.repositories.ShowtimeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomUserRepository customUserRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;

    public ReturnReservationDto createReservation(CreateReservationDto reservation) {
        Reservation newReservation = new Reservation();
        newReservation.setGeneralPrice(reservation.generalPrice());
        CustomUser user = customUserRepository.findById(reservation.userId()).
                orElseThrow(EntityNotFoundException::new);
        newReservation.setUser(user);
        List<Seat> seats = seatRepository.
                findAllById(reservation.seatIds());
        newReservation.setSeats(seats);
        Showtime showtime = showtimeRepository.
                findById(reservation.showtimeId()).orElseThrow(EntityNotFoundException::new);
        newReservation.setShowtime(showtime);

        Reservation savedReservation = reservationRepository.save(newReservation);

        return new ReturnReservationDto(
                savedReservation.getId(),
                savedReservation.getGeneralPrice(),
                savedReservation.getUser().getId() != null ? savedReservation.getUser().getId() : null,
                savedReservation.getSeats().stream().map(Seat :: getId).toList(),
                savedReservation.getShowtime() != null ? savedReservation.getShowtime().getId() : null
        );
    }

    public ReturnReservationDto updateReservation(Long id, UpdateReservationDto reservation) {
        Reservation oldReservation = reservationRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        if(reservation == null) {
            throw new IllegalArgumentException("Reservation information is null");
        } else {
            if(reservation.seatIds() != null) {
                List<Seat> seats = seatRepository.findAllById(reservation.seatIds());
                oldReservation.setSeats(seats);
            }
        }

        Reservation savedReservation = reservationRepository.save(oldReservation);

        return new ReturnReservationDto(
                savedReservation.getId(),
                savedReservation.getGeneralPrice(),
                savedReservation.getUser().getId() != null ? savedReservation.getUser().getId() : null,
                savedReservation.getSeats().stream().map(Seat :: getId).toList(),
                savedReservation.getShowtime() != null ? savedReservation.getShowtime().getId() : null
        );
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public ReturnReservationDto findReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return new ReturnReservationDto(
                reservation.getId(),
                reservation.getGeneralPrice(),
                reservation.getUser().getId() != null ? reservation.getUser().getId() : null,
                reservation.getSeats().stream().map(Seat :: getId).toList(),
                reservation.getShowtime() != null ? reservation.getShowtime().getId() : null
        );
    }

    public List<ReturnReservationDto> findAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(reservation -> new ReturnReservationDto(
                        reservation.getId(),
                        reservation.getGeneralPrice(),
                        reservation.getUser().getId()  != null ? reservation.getUser().getId() : null,
                        reservation.getSeats().stream().map(Seat :: getId).toList(),
                        reservation.getShowtime() != null ? reservation.getShowtime().getId() : null
                )).toList();
    }
}
