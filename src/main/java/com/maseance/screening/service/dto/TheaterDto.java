package com.maseance.screening.service.dto;

import lombok.Builder;

@Builder
public record TheaterDto(
        String id,
        String name,
        String address,
        String imagePath,
        String bookingPath
) { }
