package com.maseance.screening.service.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record TheaterScreeningsDto(
        TheaterDto theater,
        List<ShowtimeDto> showtimes
) {
}
