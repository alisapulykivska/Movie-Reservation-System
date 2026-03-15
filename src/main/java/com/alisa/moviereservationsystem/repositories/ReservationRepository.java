package com.alisa.moviereservationsystem.repositories;

import com.alisa.moviereservationsystem.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
