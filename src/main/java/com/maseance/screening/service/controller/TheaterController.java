package com.maseance.screening.service.controller;

import com.maseance.screening.service.dto.MovieDto;
import com.maseance.screening.service.dto.TheaterDto;
import com.maseance.screening.service.service.TheaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/theaters")
public class TheaterController {

    @Autowired
    private TheaterService theaterService;

    @GetMapping("/{id}")
    public TheaterDto getTheater(
            @PathVariable("id") UUID theaterId
    ) {
        return theaterService.getTheaterById(theaterId);
    }

    @GetMapping("/{id}/movies")
    public List<MovieDto> getMoviesByTheater(
            @PathVariable("id") UUID theaterId
    ) throws IOException {
        return theaterService.getMoviesByTheaterId(theaterId);
    }
}
