package com.maseance.screening.service.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
}
