package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.dto.createDto.ShowtimeCreateDto;
import com.alisa.moviereservationsystem.dto.returnDto.ShowtimeReturnDto;
import com.alisa.moviereservationsystem.dto.updateDto.ShowtimeUpdateDto;
import com.alisa.moviereservationsystem.exceptions.BookedHallException;
import com.alisa.moviereservationsystem.exceptions.InformationNotFoundException;
import com.alisa.moviereservationsystem.models.Hall;
import com.alisa.moviereservationsystem.models.Movie;
import com.alisa.moviereservationsystem.models.Showtime;
import com.alisa.moviereservationsystem.models.enums.ShowtimeStatus;
import com.alisa.moviereservationsystem.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@AllArgsConstructor
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final HallRepository hallRepository;

    @CacheEvict(value = "showtimes",  allEntries = true)
    public ShowtimeReturnDto createShowtime(ShowtimeCreateDto showtime) {
        Showtime newShowtime = new Showtime();

        if (showtime.dateTime().isBefore(OffsetDateTime.now())) {
            throw new IllegalArgumentException("The showtime date time is before the current date");
        }

        newShowtime.setDateTime(showtime.dateTime());
        newShowtime.setShowtimeType(showtime.showtimeType());
        newShowtime.setStatus(ShowtimeStatus.UPCOMING);
        Movie movie = movieRepository.findById(showtime.movieId())
                .orElseThrow(() -> new InformationNotFoundException("Movie not found"));
        newShowtime.setMovie(movie);
        Hall hall = hallRepository.findById(showtime.hallId())
                .orElseThrow(() -> new InformationNotFoundException("Hall not found"));

        boolean hallIsBooked = showtimeRepository.findByHall_Id(showtime.hallId())
                .stream()
                .anyMatch(s -> s.getDateTime()
                        .equals(showtime.dateTime()));

        if (hallIsBooked) {
            throw new BookedHallException("Hall is already booked for this time");
        }
        newShowtime.setHall(hall);

        Showtime savedShowtime = showtimeRepository.save(newShowtime);
        return toReturnDto(savedShowtime);
    }

    @CacheEvict(value = "showtimes", allEntries = true)
    public ShowtimeReturnDto updateShowtime(Long showtimeId, ShowtimeUpdateDto showtime) {
        Showtime oldShowtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new InformationNotFoundException("Showtime not found"));

        if (showtime.dateTime().isBefore(OffsetDateTime.now())) {
            throw new IllegalArgumentException("The showtime date time is before the current date");
        }

        oldShowtime.setDateTime(showtime.dateTime());
        oldShowtime.setShowtimeType(showtime.showtimeType());
        Hall hall = hallRepository.findById(showtime.hallId())
                .orElseThrow(() -> new InformationNotFoundException("Hall not found"));
        oldShowtime.setHall(hall);

        Showtime savedShowtime = showtimeRepository.save(oldShowtime);
        return toReturnDto(savedShowtime);
    }

    @CacheEvict(value = "showtimes", allEntries = true)
    public void deleteShowtime(Long showtimeId) {
        showtimeRepository.deleteById(showtimeId);
    }

    @Cacheable(value = "showtimes", key = "#showtimeId")
    public ShowtimeReturnDto findShowtimeById(Long showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new InformationNotFoundException("Showtime not found"));
        return toReturnDto(showtime);
    }

    @Cacheable(value = "showtimes")
    public List<ShowtimeReturnDto> findAllShowtimes() {
        return showtimeRepository.findAll()
                .stream()
                .map(this::toReturnDto)
                .toList();
    }

    @Cacheable(value = "showtimes", key = "#movieId")
    public List<ShowtimeReturnDto> getAllShowtimesForMovie(Long movieId) {
        return showtimeRepository.findByMovie_Id(movieId)
                .stream()
                .map(this::toReturnDto)
                .toList();
    }

    @Cacheable(value = "showtimes", key = "#date")
    public List<ShowtimeReturnDto> getShowtimeForDate(LocalDate date) {
        OffsetDateTime start = date.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
        OffsetDateTime end = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
        return showtimeRepository.findByDateTimeGreaterThanEqualAndDateTimeLessThanEqual(start, end)
                .stream()
                .map(this::toReturnDto)
                .toList();
    }

    private ShowtimeReturnDto toReturnDto(Showtime showtime) {
        return new ShowtimeReturnDto(
                showtime.getId(),
                showtime.getDateTime(),
                showtime.getShowtimeType(),
                showtime.getStatus(),
                showtime.getMovie().getId(),
                showtime.getHall().getId()
        );
    }
}
