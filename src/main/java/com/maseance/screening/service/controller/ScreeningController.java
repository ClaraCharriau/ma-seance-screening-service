package com.maseance.screening.service.controller;

import com.maseance.screening.service.dto.ScreeningDto;
import com.maseance.screening.service.service.ScreeningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/v1/screenings")
public class ScreeningController {
    @Autowired
    private ScreeningService screeningService;

    @GetMapping("/{id}")
    public ScreeningDto getScreening(@PathVariable("id") UUID screeningId) throws IOException {
        return screeningService.getScreeningById(screeningId);
    }
}
