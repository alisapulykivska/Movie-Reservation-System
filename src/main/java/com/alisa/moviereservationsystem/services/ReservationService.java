package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.dto.createDto.ReservationCreateDto;
import com.alisa.moviereservationsystem.dto.paymentDto.PaymentRequestDto;
import com.alisa.moviereservationsystem.dto.paymentDto.PaymentResponseDto;
import com.alisa.moviereservationsystem.dto.returnDto.ReservationReturnDto;
import com.alisa.moviereservationsystem.dto.returnDto.UpdateReservationReturnDto;
import com.alisa.moviereservationsystem.dto.updateDto.ReservationUpdateDto;
import com.alisa.moviereservationsystem.exceptions.*;
import com.alisa.moviereservationsystem.models.CustomUser;
import com.alisa.moviereservationsystem.models.Reservation;
import com.alisa.moviereservationsystem.models.Seat;
import com.alisa.moviereservationsystem.models.Showtime;
import com.alisa.moviereservationsystem.models.enums.PaymentStatus;
import com.alisa.moviereservationsystem.models.enums.ReservationStatus;
import com.alisa.moviereservationsystem.models.enums.SeatStatus;
import com.alisa.moviereservationsystem.models.enums.ShowtimeStatus;
import com.alisa.moviereservationsystem.repositories.CustomUserRepository;
import com.alisa.moviereservationsystem.repositories.ReservationRepository;
import com.alisa.moviereservationsystem.repositories.SeatRepository;
import com.alisa.moviereservationsystem.repositories.ShowtimeRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomUserRepository customUserRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final PaymentService paymentService;

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    @Transactional
    public ReservationReturnDto createReservation(ReservationCreateDto reservation) {
        log.info("Creating reservation for user {} and showtime {}", reservation.userId(), reservation.showtimeId());

        List<Seat> seats = seatRepository.findSeatsByIdIn(reservation.seatIds());

        Showtime showtime = showtimeRepository.
                findById(reservation.showtimeId()).orElseThrow(() -> new
                        InformationNotFoundException("Showtime not found"));

        if(showtime.getStatus() == ShowtimeStatus.COMPLETED) {
            throw new PastShowtimeException("Can't reserve seats for a past showtime");
        }

        if(showtime.getDateTime().isBefore(OffsetDateTime.now())) {
            showtime.setStatus(ShowtimeStatus.COMPLETED);
            throw new PastShowtimeException("Can't reserve seats for a past showtime");
        }

        boolean seatsFromWrongHall = seats.stream()
                .anyMatch(seat -> !seat.getHall().getId().equals(showtime.getHall().getId()));

        if(seatsFromWrongHall) {
            throw new WrongHallException("Can't reserve seats for a wrong hall");
        }

        boolean hasUnavailableSeats = seats.stream()
                .anyMatch(seat -> seat.getStatus() != SeatStatus.AVAILABLE);

        if(hasUnavailableSeats) {
            throw new SeatsUnavailableException("One or more seats are unavailable");
        }

        BigDecimal multiplier = switch(showtime.getShowtimeType()) {
            case PREMIERE -> new BigDecimal("1.5");
            case STANDARD -> new BigDecimal("1.0");
            case PREVIEW -> new BigDecimal("2.0");
        };

        BigDecimal generalPrice = seats.stream()
                .map(Seat::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(multiplier);

        Reservation newReservation = new Reservation();

        CustomUser user = customUserRepository.findById(reservation.userId()).
                orElseThrow(() -> new InformationNotFoundException("User not found"));
        newReservation.setUser(user);
        newReservation.setGeneralPrice(generalPrice);
        newReservation.setShowtime(showtime);
        seats.forEach(seat -> seat.setStatus(SeatStatus.HELD));
        seatRepository.saveAll(seats);
        newReservation.setSeats(seats);
        newReservation.setStatus(ReservationStatus.PENDING);

        Reservation savedReservation = reservationRepository.save(newReservation);

        log.info("Reservation created for user {} and showtime {}",
                savedReservation.getUser(), savedReservation.getShowtime());

        return toReturnDto(savedReservation);
    }

    public ReservationReturnDto confirmReservation(Long reservationId) {
        log.info("Confirming reservation {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new InformationNotFoundException("Reservation not found"));
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new InvalidReservationStatusException("Only pending reservations can be confirmed");
        }
        if(reservation.getShowtime().getStatus() == ShowtimeStatus.COMPLETED) {
            throw new PastShowtimeException("Can't confirm reservation for a past showtime");
        }

        if(reservation.getShowtime().getDateTime().isBefore(OffsetDateTime.now())) {
            Showtime showtime = reservation.getShowtime();
            showtime.setStatus(ShowtimeStatus.COMPLETED);
            throw new PastShowtimeException("Can't confirm seats for a past showtime");
        }

        PaymentResponseDto paymentResponse = paymentService.processPayment(new PaymentRequestDto(
                reservationId,
                reservation.getUser().getId(),
                reservation.getGeneralPrice())
        );

        if(paymentResponse.status() == PaymentStatus.SUCCESS) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservation.getSeats().forEach(seat -> seat.setStatus(SeatStatus.UNAVAILABLE));
            seatRepository.saveAll(reservation.getSeats());

            log.info("Reservation {} confirmed successfully", reservationId);

        } else {
            reservation.setStatus(ReservationStatus.FAILED);
            reservation.getSeats().forEach(seat -> seat.setStatus(SeatStatus.AVAILABLE));
            seatRepository.saveAll(reservation.getSeats());

            log.error("Payment failed for reservation {}", reservationId);
        }

        return toReturnDto(reservationRepository.save(reservation));
    }

    public UpdateReservationReturnDto updateReservation(Long reservationId, ReservationUpdateDto reservation) {
        log.info("Updating reservation {}", reservationId);

        Reservation oldReservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new InformationNotFoundException("Reservation not found"));

        if (oldReservation.getStatus() == ReservationStatus.CANCELLED ||
                oldReservation.getStatus() == ReservationStatus.FAILED) {
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
                .anyMatch(seat -> seat.getStatus() != SeatStatus.AVAILABLE);
        if(hasUnavailableSeats){
            throw new SeatsUnavailableException("One or more seats are unavailable");
        }

        BigDecimal oldPrice = oldReservation.getGeneralPrice();

        BigDecimal multiplier = switch(oldReservation.getShowtime().getShowtimeType()) {
            case PREMIERE -> new BigDecimal("1.5");
            case STANDARD -> new BigDecimal("1.0");
            case PREVIEW -> new BigDecimal("2.0");
        };

        BigDecimal newPrice = newSeats.stream()
                        .map(Seat::getPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .multiply(multiplier);

        BigDecimal priceDifference = newPrice.subtract(oldPrice);

        oldReservation.getSeats().forEach(seat -> seat.setStatus(SeatStatus.AVAILABLE));
        seatRepository.saveAll(oldReservation.getSeats());

        oldReservation.setSeats(newSeats);
        newSeats.forEach(seat -> seat.setStatus(SeatStatus.UNAVAILABLE));
        seatRepository.saveAll(newSeats);

        Reservation savedReservation = reservationRepository.save(oldReservation);

        log.info("Reservation {} updated, old price: {}, new price: {}, difference: {}",
                reservationId, oldPrice, newPrice, priceDifference);

        return new UpdateReservationReturnDto(
                savedReservation.getId(),
                newPrice,
                oldPrice,
                priceDifference,
                savedReservation.getStatus(),
                savedReservation.getTimeStamp(),
                savedReservation.getUser().getId(),
                savedReservation.getSeats().stream().map(Seat::getId).toList(),
                savedReservation.getShowtime().getId()
        );
    }

    public ReservationReturnDto chargeExtraPayment(Long reservationId, BigDecimal priceDifference) throws FailedPaymentException {
        log.info("Charging extra payment of {} for reservation {}", priceDifference, reservationId);

        Reservation reservation =  reservationRepository.findById(reservationId)
                .orElseThrow(() -> new InformationNotFoundException("Reservation not found"));
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new InvalidReservationStatusException("Only confirmed reservations can be charged");
        }

        PaymentResponseDto paymentResponse = paymentService.processPayment(new PaymentRequestDto(
                reservationId,
                reservation.getUser().getId(),
                priceDifference
        ));

        if(paymentResponse.status() == PaymentStatus.FAILED) {
            log.error("Extra payment of {} for reservation {} failed", priceDifference, reservationId);
            throw new FailedPaymentException("Extra payment failed");
        }

        log.info("Extra payment of {} for reservation {} successful", priceDifference, reservationId);
        return toReturnDto(reservation);
    }

    public ReservationReturnDto refundPriceDifference(Long reservationId, BigDecimal priceDifference) throws FailedPaymentException {
        log.info("Refunding price difference of {} for reservation {}", priceDifference, reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new InformationNotFoundException("Reservation not found"));
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new InvalidReservationStatusException("Only confirmed reservations can be refunded");
        }

        PaymentResponseDto refundResponse = paymentService.refundPayment(new PaymentRequestDto(
                reservationId,
                reservation.getUser().getId(),
                priceDifference
        ));

        if (refundResponse.status() == PaymentStatus.REFUND_FAILED) {
            log.error("Refund of {} for reservation {} failed", priceDifference, reservationId);
            throw new FailedPaymentException("Refund failed, please contact support");
        }

        log.info("Refund of {} for reservation {} successful", priceDifference, reservationId);
        return toReturnDto(reservation);
    }

    public void deleteReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new InformationNotFoundException("Reservation not found"));
        reservation.getSeats().forEach(seat -> seat.setStatus(SeatStatus.AVAILABLE));
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

    public ReservationReturnDto cancelReservation(Long reservationId) throws FailedPaymentException {
        log.info("Canceling reservation {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new InformationNotFoundException("Reservation not found"));

        if (reservation.getStatus() == ReservationStatus.CANCELLED ||
                reservation.getStatus() == ReservationStatus.FAILED) {
            throw new InvalidReservationStatusException
                    ("Cannot cancel a reservation that is already cancelled or failed");
        }

        if(reservation.getShowtime().getStatus() == ShowtimeStatus.COMPLETED) {
            throw new PastShowtimeException("Can't cancel a reservation for a past showtime");
        }

        if(reservation.getShowtime().getDateTime().isBefore(OffsetDateTime.now())) {
            Showtime showtime = reservation.getShowtime();
            showtime.setStatus(ShowtimeStatus.COMPLETED);
            throw new PastShowtimeException("Can't cancel a reservation for a past showtime");
        }

        if(reservation.getStatus() == (ReservationStatus.CONFIRMED)) {
            PaymentResponseDto refundResponse = paymentService.refundPayment(new PaymentRequestDto(
                    reservationId,
                    reservation.getUser().getId(),
                    reservation.getGeneralPrice()
            ));

            if(refundResponse.status() == PaymentStatus.REFUND_FAILED) {
                log.error("Refund for reservation {} failed", reservationId);
                throw new FailedPaymentException("Refund failed, please contact support");
            }
        }

        reservation.getSeats().forEach(seat -> seat.setStatus(SeatStatus.AVAILABLE));
        seatRepository.saveAll(reservation.getSeats());
        reservation.setStatus(ReservationStatus.CANCELLED);

        log.info("Reservation {} has been cancelled", reservationId);

        return toReturnDto(reservationRepository.save(reservation));
    }

    public BigDecimal getTotalRevenue() {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .map(Reservation :: getGeneralPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getReservationCountForMovie(Long movieId) {
        return reservationRepository.countByShowtime_Movie_Id(movieId);
    }

    public BigDecimal getRevenueForShowtime(Long showtimeId) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .filter(r -> r.getShowtime().getId().equals(showtimeId))
                .map(Reservation::getGeneralPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
