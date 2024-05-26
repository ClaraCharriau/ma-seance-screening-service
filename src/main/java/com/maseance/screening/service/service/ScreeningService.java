package com.maseance.screening.service.service;

import com.maseance.screening.service.dto.*;
import com.maseance.screening.service.mapper.TheaterMapper;
import com.maseance.screening.service.model.Screening;
import com.maseance.screening.service.repository.ScreeningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ScreeningService {
    @Autowired
    private ScreeningRepository screeningRepository;
    @Autowired
    private MovieService movieService;

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

    private List<MovieScreeningsDto> buildMovieScreeningsDtos(List<ScreeningDto> screenings) {
        Map<MovieDto, List<ShowtimeDto>> movieToSchedulesMap = screenings.stream()
                .collect(Collectors.groupingBy(ScreeningDto::movie,
                        Collectors.mapping(schedule -> buildShowtimeDto(schedule.id(), schedule.schedule()),
                                Collectors.toList())));

        return movieToSchedulesMap.entrySet().stream()
                .map(entry -> new MovieScreeningsDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private List<TheaterScreeningsDto> buildTheaterScreeningsDtos(List<ScreeningDto> screenings) {
        Map<TheaterDto, List<ShowtimeDto>> theaterToSchedulesMap = screenings.stream()
                .collect(Collectors.groupingBy(ScreeningDto::theater,
                        Collectors.mapping(schedule -> buildShowtimeDto(schedule.id(), schedule.schedule()),
                                Collectors.toList())));

        return theaterToSchedulesMap.entrySet().stream()
                .map(entry -> new TheaterScreeningsDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private LocalDate getDate(int day) {
        return LocalDate.now().plusDays(day - 1);
    }

    private ShowtimeDto buildShowtimeDto(String id, ScheduleDto scheduleDto) {
        return ShowtimeDto.builder()
                .id(id)
                .schedule(scheduleDto)
                .build();
    }

    private ScreeningDto buildScreeningDto(Screening screening) throws IOException {
        var movie = movieService.getMovieByTmdbId(true, screening.getMovie().getTmdbId());

        return ScreeningDto.builder()
                .id(screening.getId().toString())
                .schedule(ScheduleDto.fromDate(screening.getDate()))
                .theater(TheaterMapper.INSTANCE.toDto(screening.getTheater()))
                .movie(movie)
                .build();
    }
}
