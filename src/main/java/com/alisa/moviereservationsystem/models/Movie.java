package com.alisa.moviereservationsystem.models;

import com.alisa.moviereservationsystem.models.enums.MovieGenre;
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
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String posterUrl;

    private List<MovieGenre> genres;

    @OneToMany
    private List<Showtime> showtimes;
}
