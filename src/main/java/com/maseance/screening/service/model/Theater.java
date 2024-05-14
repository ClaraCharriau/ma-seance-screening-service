package com.maseance.screening.service.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Theater {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_theater")
    private String id;
    @Column
    private String name;
    @Column
    private String address;
    @Column(name = "image_path")
    @Nullable
    private String imagePath;
    @Column(name = "booking_path")
    private String bookingPath;
}
