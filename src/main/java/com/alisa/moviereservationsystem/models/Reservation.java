package com.alisa.moviereservationsystem.models;

import com.alisa.moviereservationsystem.models.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private Instant timeStamp;

    private Float generalPrice;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private CustomUser user;

    @ManyToMany
    @JoinTable(name = "reservation_seats")
    private List<Seat> seats;

    @ManyToOne
    @JoinColumn(name = "showtime_id")
    private Showtime showtime;
}
