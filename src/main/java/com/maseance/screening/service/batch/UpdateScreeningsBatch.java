package com.maseance.screening.service.batch;

import com.maseance.screening.service.service.ScreeningService;
import com.maseance.screening.service.service.TheaterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class UpdateScreeningsBatch {
    @Autowired
    private TheaterService theaterService;
    @Autowired
    private ScreeningService screeningService;

    public void processAllTheatersScreenings() throws IOException {
        var theaterNames = theaterService.getAllTheaterNames();

        for (var theaterName: theaterNames) {
            log.info("Updating screenings for theater with name : {}", theaterName);
            screeningService.updateScreeningsByTheaterName(theaterName);
        }
    }
}
