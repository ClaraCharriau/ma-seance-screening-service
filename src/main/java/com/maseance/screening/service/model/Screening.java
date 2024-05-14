package com.maseance.screening.service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Screening {
    @Id
    @Column(name = "id_screening")
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column
    LocalDateTime date;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_movie")
    Movie movie;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_theater")
    Theater theater;
}
