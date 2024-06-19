package com.maseance.screening.service.service;

import com.maseance.screening.service.dto.*;
import com.maseance.screening.service.mapper.TheaterMapper;
import com.maseance.screening.service.model.Movie;
import com.maseance.screening.service.model.Screening;
import com.maseance.screening.service.model.Theater;
import com.maseance.screening.service.repository.ScreeningRepository;
import com.maseance.screening.service.scrapper.GoogleShowtimesScrapper;
import com.maseance.screening.service.scrapper.response.GoogleShowtimesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.maseance.screening.service.util.DateUtils.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Slf4j
public class ScreeningService {
    @Autowired
    private ScreeningRepository screeningRepository;
    @Autowired
    private MovieService movieService;
    @Autowired
    private TheaterService theaterService;

    public ScreeningDto getScreeningById(UUID screeningId) throws IOException {
        if (!screeningRepository.existsById(screeningId)) {
            throw new ResponseStatusException(NOT_FOUND, "Could not find screening with id : " + screeningId);
        }
        var screening = screeningRepository.getReferenceById(screeningId);

        return buildScreeningDto(screening);
    }

    public List<MovieScreeningsDto> getMovieScreeningsByTheaterIdAndDay(UUID theaterId, int day) throws IOException {
        var date = getDate(day);
        var dateAndTimeLimit = date.atTime(LocalTime.MAX);
        var screenings = screeningRepository.findByTheaterIdAndDateBetween(theaterId, date.atStartOfDay(), dateAndTimeLimit);

        List<ScreeningDto> screeningDtos = new ArrayList<>();
        for (var screening : screenings) {
            screeningDtos.add(buildScreeningDto(screening));
        }

        return buildMovieScreeningsDtos(screeningDtos);
    }

    public List<TheaterScreeningsDto> getTheaterScreeningsByMovieIdAndDay(UUID movieId, int day) throws IOException {
        var date = getDate(day);
        var dateAndTimeLimit = date.atTime(LocalTime.MAX);
        var screenings = screeningRepository.findByMovieIdAndDateBetween(movieId, date.atStartOfDay(), dateAndTimeLimit);

        List<ScreeningDto> screeningDtos = new ArrayList<>();
        for (var screening : screenings) {
            screeningDtos.add(buildScreeningDto(screening));
        }

        return buildTheaterScreeningsDtos(screeningDtos);
    }

    /**
     * Update screenings in database by theater name
     * Carefully enter a theaterName that matches the name in database
     *
     * @param theaterName
     * @throws IOException
     */
    public void updateScreeningsByTheaterName(String theaterName) throws IOException {
        var googleShowtimesResponse = GoogleShowtimesScrapper.getGoogleShowtimesByTheaterName(theaterName);

        var theater = theaterService.getTheaterByName(theaterName);
        cleanScreenings(theater);

        for (GoogleShowtimesResponse.MovieShowtimes movieShowtimes : googleShowtimesResponse.movieShowtimes()) {
            addNewScreenings(movieShowtimes, theater);
        }
    }

    private void cleanScreenings(Theater theater) {
        var today = LocalDateTime.now();
        var max = today.plusMonths(1);
        log.debug("Removing screenings from theater with name: " + theater.getName() + " from : ");
        screeningRepository.deleteByTheaterIdAndDateBetween(theater.getId(), today, max);
    }

    private void addNewScreenings(GoogleShowtimesResponse.MovieShowtimes movieShowtimes, Theater theater) throws IOException {
        var movie = movieService.findMovieEntityByName(movieShowtimes.movieTitle());

        if (movie != null) {
            List<LocalDateTime> dates = buildLocalDateTimeList(movieShowtimes);
            dates.forEach(
                    date -> buildAndSaveScreening(date, movie, theater)
            );
        }
    }

    private List<LocalDateTime> buildLocalDateTimeList(GoogleShowtimesResponse.MovieShowtimes movieShowtimes) {
        List<LocalDateTime> localDateTimeSessions = new ArrayList<>();
        for (String timeSession : movieShowtimes.sessions()) {
            try {
                localDateTimeSessions.add(getLocalDateTimeFromString(movieShowtimes.day(), timeSession));
            } catch (DateTimeParseException e) {
                log.error("An error occurred while parsing date : " + timeSession + ". Skipping this screening for : " + movieShowtimes.movieTitle());
            }
        }
        return localDateTimeSessions;
    }

    private List<MovieScreeningsDto> buildMovieScreeningsDtos(List<ScreeningDto> screenings) {
        Map<MovieDto, List<ShowtimeDto>> movieToSchedulesMap = screenings.stream()
                .collect(Collectors.groupingBy(ScreeningDto::movie,
                        Collectors.mapping(schedule -> buildShowtimeDto(schedule.id(), schedule.schedule()),
                                Collectors.toList())));

        return movieToSchedulesMap.entrySet().stream()
                .map(entry -> new MovieScreeningsDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<TheaterScreeningsDto> buildTheaterScreeningsDtos(List<ScreeningDto> screenings) {
        Map<TheaterDto, List<ShowtimeDto>> theaterToSchedulesMap = screenings.stream()
                .collect(Collectors.groupingBy(ScreeningDto::theater,
                        Collectors.mapping(schedule -> buildShowtimeDto(schedule.id(), schedule.schedule()),
                                Collectors.toList())));

        return theaterToSchedulesMap.entrySet().stream()
                .map(entry -> new TheaterScreeningsDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    private LocalDate getDate(int day) {
        return LocalDate.now().plusDays(day - 1);
    }

    private void buildAndSaveScreening(LocalDateTime date, Movie movie, Theater theater) {
        var screening = buildScreening(date, movie, theater);
        screeningRepository.save(screening);
    }

    private Screening buildScreening(LocalDateTime date, Movie movie, Theater theater) {
        return new Screening(UUID.randomUUID(), date, movie, theater);
    }

    private ShowtimeDto buildShowtimeDto(String id, ScheduleDto scheduleDto) {
        return ShowtimeDto.builder()
                .id(id)
                .schedule(scheduleDto)
                .build();
    }

    private ScreeningDto buildScreeningDto(Screening screening) throws IOException {
        var movie = movieService.getMovieDtoByTmdbId(true, screening.getMovie().getTmdbId());

        return ScreeningDto.builder()
                .id(screening.getId().toString())
                .schedule(ScheduleDto.fromDate(screening.getDate()))
                .theater(TheaterMapper.INSTANCE.toDto(screening.getTheater()))
                .movie(movie)
                .build();
    }
}
