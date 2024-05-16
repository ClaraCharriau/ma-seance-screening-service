package com.maseance.screening.service.controller;

import com.maseance.screening.service.dto.MovieDto;
import com.maseance.screening.service.dto.MovieScreeningsDto;
import com.maseance.screening.service.dto.TheaterDto;
import com.maseance.screening.service.service.ScreeningService;
import com.maseance.screening.service.service.TheaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/v1/theaters")
public class TheaterController {
    @Autowired
    private TheaterService theaterService;
    @Autowired
    private ScreeningService screeningService;

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

    @GetMapping("/{id}/screenings")
    public List<MovieScreeningsDto> getScreeningsByTheater(
            @PathVariable("id") UUID theaterId
    ) throws IOException {
        return screeningService.getMovieScreeningsByTheaterId(theaterId);
    }

    @GetMapping("/search")
    public List<TheaterDto> searchTheaters(
            @RequestParam String q
    ) {
        return theaterService.searchTheaters(q);
    }
}
