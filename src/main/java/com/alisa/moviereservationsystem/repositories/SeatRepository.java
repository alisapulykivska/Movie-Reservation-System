package com.alisa.moviereservationsystem.repositories;

import com.alisa.moviereservationsystem.models.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findSeatsByIdIn(List<Long> ids);
}
