package com.maseance.screening.service.controller;

import com.maseance.screening.service.dto.ScreeningDto;
import com.maseance.screening.service.service.ScreeningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@CrossOrigin
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
