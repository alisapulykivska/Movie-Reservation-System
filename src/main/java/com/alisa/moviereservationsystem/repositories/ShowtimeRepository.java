package com.alisa.moviereservationsystem.repositories;

import com.alisa.moviereservationsystem.models.Showtime;
import com.alisa.moviereservationsystem.models.enums.ShowtimeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    List<Showtime> findByMovie_Id(Long movieId);

    List<Showtime> findByDateTimeGreaterThanEqualAndDateTimeLessThanEqual(OffsetDateTime start, OffsetDateTime end);

    List<Showtime> findByDateTimeBeforeAndStatus(OffsetDateTime dateTimeBefore, ShowtimeStatus status);

    List<Showtime> findByHall_Id(Long hallId);
}
