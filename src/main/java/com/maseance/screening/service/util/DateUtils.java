package com.maseance.screening.service.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Locale;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class DateUtils {
    private static final Locale LOCALE = Locale.FRENCH;
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("EEEE", LOCALE);
    private static final DateTimeFormatter DAY_NUMBER_FORMAT = DateTimeFormatter.ofPattern("dd", LOCALE);
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MMMM", LOCALE);
    private static final DateTimeFormatter YEAR_FORMAT = DateTimeFormatter.ofPattern("yyyy", LOCALE);
    private static final DateTimeFormatter HOUR_FORMAT = DateTimeFormatter.ofPattern("HH:mm", LOCALE);

    public static String getDayName(LocalDateTime date) {
        return DAY_FORMAT.format(date);
    }

    public static String getDayNumber(LocalDateTime date) {
        return DAY_NUMBER_FORMAT.format(date);
    }

    public static String getMonth(LocalDateTime date) {
        return MONTH_FORMAT.format(date);
    }

    public static String getYear(LocalDateTime date) {
        return YEAR_FORMAT.format(date);
    }

    public static String getHourly(LocalDateTime date) {
        return HOUR_FORMAT.format(date);
    }

    public static LocalDateTime getLocalDateTimeFromString(String day, String time) {
        log.debug(String.format("Parsing date and time : %s %s to LocalDateTime.", day, time));
        return LocalDateTime.of(getLocalDateFromString(day), LocalTime.parse(time));
    }

    private LocalDate getLocalDateFromString(String dayString) {
        return switch (dayString) {
            case "Aujourd'hui" -> LocalDate.now();
            case "Demain" -> LocalDate.now().plusDays(1);
            default -> getLocalDateFromStrings(dayString.split(" "));
        };
    }

    private LocalDate getLocalDateFromStrings(String[] dateParts) {
        var year = LocalDate.now().getYear();
        return LocalDate.of(year, getMonthNumber(dateParts[2]), Integer.parseInt(dateParts[1]));
    }

    private int getMonthNumber(String monthString) {
        return MONTH_FORMAT.parse(monthString).get(ChronoField.MONTH_OF_YEAR);
    }
}
