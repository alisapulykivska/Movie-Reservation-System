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

    @Query("SELECT m FROM Movie m JOIN m.showtimes s JOIN Reservation r ON r.showtime = s " +
            "GROUP BY m ORDER BY COUNT(r) DESC")
    List<Movie> findMostPopularMovies();

    List<Movie> findByTitleContainingIgnoreCase(String title);
}
