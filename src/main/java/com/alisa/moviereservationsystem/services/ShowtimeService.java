package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.dto.createDto.CreateShowtimeDto;
import com.alisa.moviereservationsystem.dto.returnDto.ReturnSeatDto;
import com.alisa.moviereservationsystem.dto.returnDto.ReturnShowtimeDto;
import com.alisa.moviereservationsystem.dto.updateDto.UpdateHallDto;
import com.alisa.moviereservationsystem.dto.updateDto.UpdateShowtimeDto;
import com.alisa.moviereservationsystem.models.Movie;
import com.alisa.moviereservationsystem.models.Showtime;
import com.alisa.moviereservationsystem.repositories.MovieRepository;
import com.alisa.moviereservationsystem.repositories.ShowtimeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;

    public ReturnShowtimeDto createShowtime(CreateShowtimeDto showtime) {
        Showtime newShowtime = new Showtime();
        newShowtime.setDateTime(showtime.dateTime());
        newShowtime.setShowtimeType(showtime.showtimeType());
        Movie movie = movieRepository.findById(showtime.movieId()).orElseThrow(EntityNotFoundException::new);
        newShowtime.setMovie(movie);

        Showtime savedShowtime = showtimeRepository.save(newShowtime);

        return new ReturnShowtimeDto(
                savedShowtime.getId(),
                savedShowtime.getDateTime(),
                savedShowtime.getShowtimeType(),
                savedShowtime.getMovie() != null ? savedShowtime.getMovie().getId() : null
        );
    }

    public ReturnShowtimeDto updateShowtime(Long id, UpdateShowtimeDto showtime) {
        Showtime oldShowtime = showtimeRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        if(showtime == null) {
            throw new IllegalArgumentException("Showtime information is null");
        } else {
            if(showtime.dateTime() != null) {
                oldShowtime.setDateTime(showtime.dateTime());
            }
            if(showtime.showtimeType() != null) {
                oldShowtime.setShowtimeType(showtime.showtimeType());
            }
        }
        Showtime savedShowtime = showtimeRepository.save(oldShowtime);

        return new ReturnShowtimeDto(
                savedShowtime.getId(),
                savedShowtime.getDateTime(),
                savedShowtime.getShowtimeType(),
                savedShowtime.getMovie() != null ? savedShowtime.getMovie().getId() : null
        );
    }

    public void deleteShowtime(Long id) {
        showtimeRepository.deleteById(id);
    }

    public ReturnShowtimeDto findShowtimeById(Long id) {
        Showtime showtime = showtimeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return new ReturnShowtimeDto(
                showtime.getId(),
                showtime.getDateTime(),
                showtime.getShowtimeType(),
                showtime.getMovie() != null ? showtime.getMovie().getId() : null
        );
    }

    public List<ReturnShowtimeDto> findAllShowtimes() {
        return showtimeRepository.findAll()
                .stream()
                .map(showtime -> new ReturnShowtimeDto(
                        showtime.getId(),
                        showtime.getDateTime(),
                        showtime.getShowtimeType(),
                        showtime.getMovie() != null ? showtime.getMovie().getId() : null
                )).toList();
    }

    public List<ReturnShowtimeDto> getAllShowtimesForMovie(Long movieId) {
        return showtimeRepository.findByMovie_Id(movieId)
                .stream()
                .map(showtime -> new ReturnShowtimeDto(
                        showtime.getId(),
                        showtime.getDateTime(),
                        showtime.getShowtimeType(),
                        showtime.getMovie() != null ? showtime.getMovie().getId() : null
                )).toList();
    }
}
