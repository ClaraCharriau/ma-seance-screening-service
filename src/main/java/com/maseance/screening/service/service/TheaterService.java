package com.maseance.screening.service.service;

import com.maseance.screening.service.dto.MovieDto;
import com.maseance.screening.service.dto.TheaterDto;
import com.maseance.screening.service.mapper.TheaterMapper;
import com.maseance.screening.service.model.Movie;
import com.maseance.screening.service.model.Theater;
import com.maseance.screening.service.repository.MovieRepository;
import com.maseance.screening.service.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class TheaterService {
    @Autowired
    private TheaterRepository theaterRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieService movieService;

    public TheaterDto getTheaterById(UUID theaterId) {
        if (!theaterRepository.existsById(theaterId)) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find theater with id : " + theaterId);
        }
        var theaterEntity = theaterRepository.getReferenceById(theaterId);
        return TheaterMapper.INSTANCE.toDto(theaterEntity);
    }

    public Theater getTheaterByName(String theaterName) {
        if (!theaterRepository.existsByName(theaterName)) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find theater with name : " + theaterName);
        }
        return theaterRepository.getTheaterByName(theaterName);
    }

    public List<MovieDto> getMoviesByTheaterId(UUID theaterId) throws IOException {
        if (!theaterRepository.existsById(theaterId)) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find theater with id : " + theaterId);
        }

        List<Movie> movieList = movieRepository.getMoviesByTheaterId(theaterId).stream().distinct().toList();
        return movieService.getMoviesByTmdbId(movieList);
    }

    public List<TheaterDto> searchTheaters(String query) {
        var theaterEntities = theaterRepository.findByNameOrAddressContainingIgnoreCase(query, query);
        return TheaterMapper.INSTANCE.toDtos(theaterEntities);
    }
}
