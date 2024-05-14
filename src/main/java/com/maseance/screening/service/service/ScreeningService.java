package com.maseance.screening.service.service;

import com.maseance.screening.service.dto.ScheduleDto;
import com.maseance.screening.service.dto.ScreeningDto;
import com.maseance.screening.service.mapper.TheaterMapper;
import com.maseance.screening.service.model.Screening;
import com.maseance.screening.service.repository.ScreeningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.UUID;

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
