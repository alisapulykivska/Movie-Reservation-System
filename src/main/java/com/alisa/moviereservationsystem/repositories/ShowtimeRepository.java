package com.alisa.moviereservationsystem.repositories;

import com.alisa.moviereservationsystem.models.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
}
