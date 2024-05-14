package com.maseance.screening.service.dto;

import lombok.Builder;

@Builder
public record ScreeningDto(String id,
                           ScheduleDto schedule,
                           MovieDto movie,
                           TheaterDto theater) {
}
