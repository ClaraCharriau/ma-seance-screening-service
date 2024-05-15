package com.maseance.screening.service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    @Column(name = "id_movie")
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(length = 12, unique = true, name = "id_tmdb")
    String tmdbId;
}
