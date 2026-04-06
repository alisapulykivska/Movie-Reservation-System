package com.alisa.moviereservationsystem.schedulers;

import com.alisa.moviereservationsystem.models.Reservation;
import com.alisa.moviereservationsystem.models.enums.ReservationStatus;
import com.alisa.moviereservationsystem.models.enums.SeatStatus;
import com.alisa.moviereservationsystem.repositories.ReservationRepository;
import com.alisa.moviereservationsystem.repositories.SeatRepository;
import com.alisa.moviereservationsystem.services.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Transactional
@Component
@AllArgsConstructor
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    @Scheduled(fixedRate = 60000)
    public void expirePendingReservations() {
        Instant cutoff = Instant.now().minus(15, ChronoUnit.MINUTES);
        List<Reservation> expiredReservations = reservationRepository
                .findByStatusAndTimeStampBefore(ReservationStatus.Pending, cutoff);

        expiredReservations.forEach(reservation -> {
            reservation.getSeats().forEach(seat -> seat.setStatus(SeatStatus.Available));
            seatRepository.saveAll(reservation.getSeats());
            reservation.setStatus(ReservationStatus.Failed);
        });
            reservationRepository.saveAll(expiredReservations);
    }
}
