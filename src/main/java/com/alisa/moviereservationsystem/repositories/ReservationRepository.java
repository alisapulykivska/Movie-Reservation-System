package com.alisa.moviereservationsystem.repositories;

import com.alisa.moviereservationsystem.models.Reservation;
import com.alisa.moviereservationsystem.models.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser_Id(Long userId);

    List<Reservation> findByStatusAndTimeStampBefore(ReservationStatus status, Instant timeStampBefore);

    Long countByShowtime_Movie_Id(Long movieId);
}
