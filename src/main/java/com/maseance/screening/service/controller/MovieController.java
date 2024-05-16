package com.maseance.screening.service.controller;

import com.maseance.screening.service.dto.MovieDto;
import com.maseance.screening.service.dto.TheaterScreeningsDto;
import com.maseance.screening.service.service.MovieService;
import com.maseance.screening.service.service.ScreeningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/v1/movies")
public class MovieController {
    @Autowired
    private MovieService movieService;
    @Autowired
    private ScreeningService screeningService;

    @GetMapping("/{id}")
    public MovieDto getMovie(
            @PathVariable("id") UUID movieId,
            @RequestParam(required = false, defaultValue = "false", name = "extended_infos") boolean extendedInfos) throws IOException {
        return movieService.getMovieById(extendedInfos, movieId);
    }

    @GetMapping("/{id}/screenings")
    public List<TheaterScreeningsDto> getScreeningsByMovie(
            @PathVariable("id") UUID movieId
    ) throws IOException {
        return screeningService.getTheaterScreeningsByMovieId(movieId);
    }

    @GetMapping("/currently")
    public List<MovieDto> getCurrentlyPlayingMovies(
            @RequestParam(required = false, defaultValue = "false", name = "extended_infos") boolean extendedInfos) throws IOException {
        return movieService.getCurrentlyPlayingMovies(extendedInfos);
    }

    @GetMapping("/search")
    public List<MovieDto> searchMovies(
            @RequestParam String q
    ) throws IOException {
        return movieService.searchMovies(q);
    }
}
