package com.maseance.screening.service.scrapper.response;

import java.util.List;

public record GoogleShowtimesResponse(
        String theaterName,
        List<MovieShowtimes> movieShowtimes
) {
    public record MovieShowtimes(
            String movieTitle,
            String day,
            List<String> sessions
    ) {
    }
}
