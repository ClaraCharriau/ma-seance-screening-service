package com.maseance.screening.service.service;

import com.maseance.screening.service.dto.TheaterDto;
import com.maseance.screening.service.mapper.TheaterMapper;
import com.maseance.screening.service.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class TheaterService {
    @Autowired
    private TheaterRepository theaterRepository;

    public TheaterDto getTheaterById(String theaterId) {
        if (!theaterRepository.existsById(theaterId)) {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find theater with id : " + theaterId);
        }
        var theaterEntity = theaterRepository.getReferenceById(theaterId);
        return TheaterMapper.INSTANCE.toDto(theaterEntity);
    }
}
