package com.maseance.screening.service.dto;

import com.maseance.screening.service.util.DateUtils;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ScheduleDto(LocalDateTime date,
                          String dayName,
                          String dayNumber,
                          String month,
                          String year,
                          String hourly) {

    public static ScheduleDto fromDate(LocalDateTime date) {
        return ScheduleDto.builder()
                .date(date)
                .dayName(DateUtils.getDayName(date))
                .dayNumber(DateUtils.getDayNumber(date))
                .month(DateUtils.getMonth(date))
                .year(DateUtils.getYear(date))
                .hourly(DateUtils.getHourly(date))
                .build();
    }
}
