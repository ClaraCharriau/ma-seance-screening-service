package com.maseance.screening.service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
