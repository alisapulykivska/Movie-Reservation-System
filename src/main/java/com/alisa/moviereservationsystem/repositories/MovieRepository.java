package com.alisa.moviereservationsystem.repositories;

import com.alisa.moviereservationsystem.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
}
