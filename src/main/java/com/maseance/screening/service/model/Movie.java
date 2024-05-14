package com.maseance.screening.service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Movie {
    @Id
    @Column(name = "id_movie")
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(length = 12, unique = true, name = "id_tmdb")
    String tmdbId;
}
