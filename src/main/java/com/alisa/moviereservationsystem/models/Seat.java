package com.alisa.moviereservationsystem.models;

import com.alisa.moviereservationsystem.models.enums.SeatStatus;
import com.alisa.moviereservationsystem.models.enums.SeatType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer seatNumber;

    private Integer rowNumber;

    private Float price;

    @Enumerated(EnumType.STRING)
    private SeatType type;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @ManyToOne
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @ManyToMany
    private List<Showtime> showtimes;

    @ManyToMany
    private List<Reservation> reservations;
}
