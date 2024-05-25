package com.maseance.screening.service.dto;

import lombok.Builder;

@Builder
public record ShowtimeDto(
        String id,
        ScheduleDto schedule
) {
}
