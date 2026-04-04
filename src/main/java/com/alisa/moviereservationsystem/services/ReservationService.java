package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.dto.createDto.ReservationCreateDto;
import com.alisa.moviereservationsystem.dto.returnDto.ReservationReturnDto;
import com.alisa.moviereservationsystem.dto.updateDto.ReservationUpdateDto;
import com.alisa.moviereservationsystem.exceptions.*;
import com.alisa.moviereservationsystem.models.CustomUser;
import com.alisa.moviereservationsystem.models.Reservation;
import com.alisa.moviereservationsystem.models.Seat;
import com.alisa.moviereservationsystem.models.Showtime;
import com.alisa.moviereservationsystem.models.enums.ReservationStatus;
import com.alisa.moviereservationsystem.models.enums.SeatStatus;
import com.alisa.moviereservationsystem.models.enums.ShowtimeStatus;
import com.alisa.moviereservationsystem.repositories.CustomUserRepository;
import com.alisa.moviereservationsystem.repositories.ReservationRepository;
import com.alisa.moviereservationsystem.repositories.SeatRepository;
import com.alisa.moviereservationsystem.repositories.ShowtimeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomUserRepository customUserRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;

    private ReservationReturnDto toDto(Reservation reservation) {
        return new ReservationReturnDto(
                reservation.getId(),
                reservation.getGeneralPrice(),
                reservation.getStatus(),
                reservation.getTimeStamp(),
                reservation.getUser().getId(),
                reservation.getSeats().stream().map(Seat :: getId).toList(),
                reservation.getShowtime().getId()
        );
    }

    @Transactional
    public ReservationReturnDto createReservation(ReservationCreateDto reservation) {
        List<Seat> seats = seatRepository.findSeatsWithLock(reservation.seatIds());

        boolean hasUnavailableSeats = seats.stream()
                .anyMatch(seat -> seat.getStatus() == SeatStatus.Unavailable);

        if(hasUnavailableSeats){
            throw new SeatsUnavailableException("One or more seats are unavailable");
        }

        Showtime showtime = showtimeRepository.
                findById(reservation.showtimeId()).orElseThrow(() -> new
                        InformationNotFoundException("Showtime", reservation.showtimeId()));

        if(showtime.getStatus() == ShowtimeStatus.Completed) {
            throw new PastShowtimeException("Can't reserve seats for a past showtime");
        }

        Float multiplier = switch(showtime.getShowtimeType()) {
            case Premiere -> 1.5f;
            case Standard -> 1.0f;
            case Preview -> 2.0f;
        };

        Float generalPrice = seats.stream()
                .map(Seat::getPrice)
                .reduce(0f, Float::sum) * multiplier;

        Reservation newReservation = new Reservation();

        CustomUser user = customUserRepository.findById(reservation.userId()).
                orElseThrow(() -> new InformationNotFoundException("CustomUser", reservation.userId()));
        newReservation.setUser(user);
        newReservation.setGeneralPrice(generalPrice);
        newReservation.setSeats(seats);
        newReservation.setShowtime(showtime);
        seats.forEach(seat -> seat.setStatus(SeatStatus.Unavailable));
        seatRepository.saveAll(seats);
        newReservation.setStatus(ReservationStatus.Pending);

        Reservation savedReservation = reservationRepository.save(newReservation);

        return toDto(savedReservation);
    }

    public ReservationReturnDto confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new InformationNotFoundException("Reservation", reservationId));
        if (reservation.getStatus() != ReservationStatus.Pending) {
            throw new InvalidReservationStatusException("Only pending reservations can be confirmed");
        }
        if(reservation.getShowtime().getStatus() == ShowtimeStatus.Completed) {
            throw new PastShowtimeException("Can't confirm reservation for a past showtime");
        }

        reservation.setStatus(ReservationStatus.Confirmed);
        Reservation savedReservation = reservationRepository.save(reservation);
        return toDto(savedReservation);
    }

    public ReservationReturnDto updateReservation(Long id, ReservationUpdateDto reservation) {
        Reservation oldReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException("Reservation", id));

        if (oldReservation.getStatus() == ReservationStatus.Cancelled ||
                oldReservation.getStatus() == ReservationStatus.Failed) {
            throw new InvalidReservationStatusException
                    ("Cannot update a cancelled or failed reservation");
        }

        if(reservation == null) {
            throw new InformationIsNullException("Reservation information is null");
        } else {
            if(reservation.seatIds() != null) {
                oldReservation.getSeats().forEach(seat -> seat.setStatus(SeatStatus.Available));
                seatRepository.saveAll(oldReservation.getSeats());

                List<Seat> newSeats = seatRepository.findAllById(reservation.seatIds());
                boolean hasUnavailableSeats = newSeats.stream()
                        .anyMatch(seat -> seat.getStatus() == SeatStatus.Unavailable);
                if(hasUnavailableSeats){
                    throw new SeatsUnavailableException("One or more seats are unavailable");
                }

                oldReservation.setSeats(newSeats);
                newSeats.forEach(seat -> seat.setStatus(SeatStatus.Unavailable));
                seatRepository.saveAll(newSeats);
            }
        }
        Reservation savedReservation = reservationRepository.save(oldReservation);
        return toDto(savedReservation);
    }

    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException("Reservation", id));
        reservation.getSeats().forEach(seat -> seat.setStatus(SeatStatus.Available));
        seatRepository.saveAll(reservation.getSeats());
        reservationRepository.deleteById(id);
    }

    public ReservationReturnDto findReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException("Reservation", id));
        return toDto(reservation);
    }

    public List<ReservationReturnDto> findAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(this :: toDto)
                .toList();
    }

    public List<ReservationReturnDto> getAllReservationsForUser(Long userId) {
        return reservationRepository.findByUser_Id(userId)
                .stream()
                .map(this :: toDto)
                .toList();
    }

    public ReservationReturnDto cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException("Reservation", id));

        if (reservation.getStatus() == ReservationStatus.Cancelled ||
                reservation.getStatus() == ReservationStatus.Failed) {
            throw new InvalidReservationStatusException
                    ("Cannot cancel a reservation that is already cancelled or failed");
        }

        if(reservation.getShowtime().getStatus() == ShowtimeStatus.Completed) {
            throw new PastShowtimeException("Can't cancel a reservation for a past showtime");
        }

        reservation.getSeats().forEach(seat -> seat.setStatus(SeatStatus.Available));
        seatRepository.saveAll(reservation.getSeats());

        reservation.setStatus(ReservationStatus.Cancelled);
        Reservation savedReservation = reservationRepository.save(reservation);
        return toDto(savedReservation);
    }

    public Float getTotalRevenue() {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() == ReservationStatus.Confirmed)
                .map(Reservation :: getGeneralPrice)
                .reduce(0f, Float ::sum);
    }

    public Long getReservationCountForMovie(Long movieId) {
        return reservationRepository.countByShowtime_Movie_Id(movieId);
    }
}
