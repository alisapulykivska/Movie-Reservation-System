package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.models.Showtime;
import com.alisa.moviereservationsystem.repositories.ShowtimeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ShowtimeService {

    private ShowtimeRepository showtimeRepository;

    public Showtime createShowtime(Showtime showtime) {
        return showtimeRepository.save(showtime);
    }

    public Showtime updateShowtime(Long id, Showtime showtime) {
        Showtime oldShowtime = showtimeRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        if(showtime == null) {
            throw new IllegalArgumentException("Showtime information is null");
        } else {
            if(showtime.getDateTime() != null) {
                oldShowtime.setDateTime(showtime.getDateTime());
            }
            if(showtime.getShowtimeType() != null) {
                oldShowtime.setShowtimeType(showtime.getShowtimeType());
            }
        }
        return showtimeRepository.save(oldShowtime);
    }

    public void deleteShowtime(Long id) {
        showtimeRepository.deleteById(id);
    }

    public Showtime findShowtimeById(Long id) {
        return showtimeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<Showtime> findAllShowtimes() {
        return showtimeRepository.findAll();
    }
}
