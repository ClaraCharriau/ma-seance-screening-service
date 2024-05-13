package com.maseance.screening.service.controller;

import com.maseance.screening.service.dto.MovieDto;
import com.maseance.screening.service.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/currently")
    public List<MovieDto> getCurrentlyPlayingMovies(
            @RequestParam(required = false, defaultValue = "false", name = "extended_infos") boolean extendedInfos) throws IOException {
        return movieService.getCurrentlyPlayingMovies(extendedInfos);
    }
}
