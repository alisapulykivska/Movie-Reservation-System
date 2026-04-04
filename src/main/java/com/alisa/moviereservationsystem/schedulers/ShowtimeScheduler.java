package com.alisa.moviereservationsystem.schedulers;

import com.alisa.moviereservationsystem.models.Showtime;
import com.alisa.moviereservationsystem.models.enums.ShowtimeStatus;
import com.alisa.moviereservationsystem.repositories.ShowtimeRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Transactional
@Component
@AllArgsConstructor
public class ShowtimeScheduler {

    private final ShowtimeRepository showtimeRepository;

    @Scheduled(fixedRate = 3600000)
    public void completePassedShowtimes() {
        List<Showtime> passedShowtimes = showtimeRepository
                .findByDateTimeBeforeAndStatus(OffsetDateTime.now(), ShowtimeStatus.Upcoming);
        passedShowtimes.forEach(showtime -> showtime.setStatus(ShowtimeStatus.Completed));
        showtimeRepository.saveAll(passedShowtimes);
    }
}
