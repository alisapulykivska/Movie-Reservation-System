package com.alisa.moviereservationsystem.models;

import com.alisa.moviereservationsystem.models.enums.ShowtimeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private OffsetDateTime dateTime;

    private ShowtimeType showtimeType;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToMany
    private List<Seat> seats;

    @OneToOne
    private Reservation reservation;
}
