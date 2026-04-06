package com.alisa.moviereservationsystem.repositories;

import com.alisa.moviereservationsystem.dto.returnDto.MovieReturnDto;
import com.alisa.moviereservationsystem.models.Movie;
import com.alisa.moviereservationsystem.models.enums.MovieGenre;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query(value = "SELECT m.* FROM movie m " +
            "JOIN showtime s ON s.movie_id = m.id " +
            "JOIN reservation r ON r.showtime_id = s.id " +
            "GROUP BY m.id ORDER BY COUNT(r.id) DESC",
            nativeQuery = true)
    List<Movie> findAllSortedByReservationCountDesc();

    List<Movie> findByTitleContainingIgnoreCase(String title);
}
