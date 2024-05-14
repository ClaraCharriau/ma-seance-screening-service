package com.maseance.screening.service.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record MovieScreeningsDto(
        MovieDto movie,
        List<ScheduleDto> schedule
) {
}
