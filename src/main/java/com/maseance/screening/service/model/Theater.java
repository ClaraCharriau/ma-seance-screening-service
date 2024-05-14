package com.maseance.screening.service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Theater {
    @Id
    @Column(name = "id_theater")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String name;

    @Column
    private String address;

    @Column(name = "image_path", nullable = true)
    private String imagePath;

    @Column(name = "booking_path")
    private String bookingPath;
}
