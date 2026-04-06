package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.dto.createDto.ShowtimeCreateDto;
import com.alisa.moviereservationsystem.dto.returnDto.ShowtimeReturnDto;
import com.alisa.moviereservationsystem.dto.updateDto.ShowtimeUpdateDto;
import com.alisa.moviereservationsystem.exceptions.BookedHallException;
import com.alisa.moviereservationsystem.exceptions.InformationIsNullException;
import com.alisa.moviereservationsystem.exceptions.InformationNotFoundException;
import com.alisa.moviereservationsystem.models.Hall;
import com.alisa.moviereservationsystem.models.Movie;
import com.alisa.moviereservationsystem.models.Reservation;
import com.alisa.moviereservationsystem.models.Showtime;
import com.alisa.moviereservationsystem.models.enums.ReservationStatus;
import com.alisa.moviereservationsystem.models.enums.ShowtimeStatus;
import com.alisa.moviereservationsystem.repositories.*;
import lombok.AllArgsConstructor;
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

    public ShowtimeReturnDto createShowtime(ShowtimeCreateDto showtime) {
        Showtime newShowtime = new Showtime();
        newShowtime.setDateTime(showtime.dateTime());
        newShowtime.setShowtimeType(showtime.showtimeType());
        newShowtime.setStatus(ShowtimeStatus.Upcoming);
        Movie movie = movieRepository.findById(showtime.movieId())
                .orElseThrow(() -> new InformationNotFoundException("Movie", showtime.movieId()));
        newShowtime.setMovie(movie);
        Hall hall = hallRepository.findById(showtime.hallId())
                .orElseThrow(()  -> new InformationNotFoundException("Hall", showtime.hallId()));

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

    public ShowtimeReturnDto updateShowtime(Long showtimeId, ShowtimeUpdateDto showtime) {
        Showtime oldShowtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new InformationNotFoundException("Showtime", showtimeId));
        if(showtime == null) {
            throw new InformationIsNullException("Showtime information is null");
        } else {
            if(showtime.dateTime() != null) {
                oldShowtime.setDateTime(showtime.dateTime());
            }
            if(showtime.showtimeType() != null) {
                oldShowtime.setShowtimeType(showtime.showtimeType());
            }
            if(showtime.hallId() != null) {
                Hall hall = hallRepository.findById(showtime.hallId())
                        .orElseThrow(() -> new InformationNotFoundException("Hall", showtime.hallId()));
                oldShowtime.setHall(hall);
            }
        }
        Showtime savedShowtime = showtimeRepository.save(oldShowtime);

        return toReturnDto(savedShowtime);
    }

    public void deleteShowtime(Long showtimeId) {
        showtimeRepository.deleteById(showtimeId);
    }

    public ShowtimeReturnDto findShowtimeById(Long id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException("Showtime", id));
        return toReturnDto(showtime);
    }

    public List<ShowtimeReturnDto> findAllShowtimes() {
        return showtimeRepository.findAll()
                .stream()
                .map(this :: toReturnDto)
                .toList();
    }

    public List<ShowtimeReturnDto> getAllShowtimesForMovie(Long movieId) {
        return showtimeRepository.findByMovie_Id(movieId)
                .stream()
                .map(this::toReturnDto)
                .toList();
    }

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
