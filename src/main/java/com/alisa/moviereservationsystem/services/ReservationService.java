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
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomUserRepository customUserRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;

    @Transactional
    public ReservationReturnDto createReservation(ReservationCreateDto reservation) {
        List<Seat> seats = seatRepository.findSeatsByIdIn(reservation.seatIds());

        Showtime showtime = showtimeRepository.
                findById(reservation.showtimeId()).orElseThrow(() -> new
                        InformationNotFoundException("Showtime not found"));

        if(showtime.getStatus() == ShowtimeStatus.Completed) {
            throw new PastShowtimeException("Can't reserve seats for a past showtime");
        }

        if(showtime.getDateTime().isBefore(OffsetDateTime.now())) {
            showtime.setStatus(ShowtimeStatus.Completed);
            throw new PastShowtimeException("Can't reserve seats for a past showtime");
        }

        boolean seatsFromWrongHall = seats.stream()
                .anyMatch(seat -> !seat.getHall().getId().equals(showtime.getHall().getId()));

        if(seatsFromWrongHall) {
            throw new WrongHallException("Can't reserve seats for a wrong hall");
        }

        boolean hasUnavailableSeats = seats.stream()
                .anyMatch(seat -> seat.getStatus() != SeatStatus.Available);

        if(hasUnavailableSeats) {
            throw new SeatsUnavailableException("One or more seats are unavailable");
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
                orElseThrow(() -> new InformationNotFoundException("User not found"));
        newReservation.setUser(user);
        newReservation.setGeneralPrice(generalPrice);
        newReservation.setShowtime(showtime);
        seats.forEach(seat -> seat.setStatus(SeatStatus.Held));
        seatRepository.saveAll(seats);
        newReservation.setSeats(seats);
        newReservation.setStatus(ReservationStatus.Pending);

        Reservation savedReservation = reservationRepository.save(newReservation);

        return toReturnDto(savedReservation);
    }

    public ReservationReturnDto confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new InformationNotFoundException("Reservation not found"));
        if (reservation.getStatus() != ReservationStatus.Pending) {
            throw new InvalidReservationStatusException("Only pending reservations can be confirmed");
        }
        if(reservation.getShowtime().getStatus() == ShowtimeStatus.Completed) {
            throw new PastShowtimeException("Can't confirm reservation for a past showtime");
        }

        if(reservation.getShowtime().getDateTime().isBefore(OffsetDateTime.now())) {
            Showtime showtime = reservation.getShowtime();
            showtime.setStatus(ShowtimeStatus.Completed);
            throw new PastShowtimeException("Can't confirm seats for a past showtime");
        }

        reservation.setStatus(ReservationStatus.Confirmed);
        reservation.getSeats().forEach(seat -> seat.setStatus(SeatStatus.Unavailable));
        Reservation savedReservation = reservationRepository.save(reservation);
        return toReturnDto(savedReservation);
    }

    public ReservationReturnDto updateReservation(Long reservationId, ReservationUpdateDto reservation) {
        Reservation oldReservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new InformationNotFoundException("Reservation not found"));

        if (oldReservation.getStatus() == ReservationStatus.Cancelled ||
                oldReservation.getStatus() == ReservationStatus.Failed) {
            throw new InvalidReservationStatusException
                    ("Cannot update a cancelled or failed reservation");
        }

        List<Seat> newSeats = seatRepository.findAllById(reservation.seatIds());

        boolean seatsFromWrongHall = newSeats.stream()
                .anyMatch(seat -> !seat.getHall().getId().equals(oldReservation.getShowtime().getHall().getId()));
        if (seatsFromWrongHall) {
            throw new WrongHallException("Can't reserve seats for a wrong hall");
        }

        boolean hasUnavailableSeats = newSeats.stream()
                .anyMatch(seat -> seat.getStatus() != SeatStatus.Available);
        if(hasUnavailableSeats){
            throw new SeatsUnavailableException("One or more seats are unavailable");
        }

        oldReservation.getSeats().forEach(seat -> seat.setStatus(SeatStatus.Available));
        seatRepository.saveAll(oldReservation.getSeats());

        oldReservation.setSeats(newSeats);
        newSeats.forEach(seat -> seat.setStatus(SeatStatus.Unavailable));
        seatRepository.saveAll(newSeats);

        Reservation savedReservation = reservationRepository.save(oldReservation);
        return toReturnDto(savedReservation);
    }

    public void deleteReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new InformationNotFoundException("Reservation not found"));
        reservation.getSeats().forEach(seat -> seat.setStatus(SeatStatus.Available));
        seatRepository.saveAll(reservation.getSeats());
        reservationRepository.deleteById(reservationId);
    }

    public ReservationReturnDto findReservationById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new InformationNotFoundException("Reservation not found"));
        return toReturnDto(reservation);
    }

    public List<ReservationReturnDto> findAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(this :: toReturnDto)
                .toList();
    }

    public List<ReservationReturnDto> findAllReservationsForUser(Long userId) {
        return reservationRepository.findByUser_Id(userId)
                .stream()
                .map(this :: toReturnDto)
                .toList();
    }

    public ReservationReturnDto cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new InformationNotFoundException("Reservation not found"));

        if (reservation.getStatus() == ReservationStatus.Cancelled ||
                reservation.getStatus() == ReservationStatus.Failed) {
            throw new InvalidReservationStatusException
                    ("Cannot cancel a reservation that is already cancelled or failed");
        }

        if(reservation.getShowtime().getStatus() == ShowtimeStatus.Completed) {
            throw new PastShowtimeException("Can't cancel a reservation for a past showtime");
        }

        if(reservation.getShowtime().getDateTime().isBefore(OffsetDateTime.now())) {
            Showtime showtime = reservation.getShowtime();
            showtime.setStatus(ShowtimeStatus.Completed);
            throw new PastShowtimeException("Can't cancel a reservation for a past showtime");
        }

        reservation.getSeats().forEach(seat -> seat.setStatus(SeatStatus.Available));
        seatRepository.saveAll(reservation.getSeats());

        reservation.setStatus(ReservationStatus.Cancelled);
        Reservation savedReservation = reservationRepository.save(reservation);
        return toReturnDto(savedReservation);
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

    public Float getRevenueForShowtime(Long showtimeId) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() == ReservationStatus.Confirmed)
                .filter(r -> r.getShowtime().getId().equals(showtimeId))
                .map(Reservation::getGeneralPrice)
                .reduce(0f, Float::sum);
    }

    private ReservationReturnDto toReturnDto(Reservation reservation) {
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
}
