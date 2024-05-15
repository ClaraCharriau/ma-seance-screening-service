package com.maseance.screening.service.mapper;

import com.maseance.screening.service.dto.TheaterDto;
import com.maseance.screening.service.model.Theater;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TheaterMapper {

    TheaterMapper INSTANCE = Mappers.getMapper(TheaterMapper.class);

    TheaterDto toDto(Theater theaterEntity);

    List<TheaterDto> toDtos(List<Theater> theaters);
}
