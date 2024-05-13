package com.maseance.screening.service.dto;

import jakarta.annotation.Nullable;
import lombok.Builder;

import java.util.List;

@Builder
public record MovieDto(
        String id,
        String title,
        String posterLink,
        @Nullable
        String releaseDate,
        @Nullable
        Integer duration,
        @Nullable
        String resume,
        @Nullable
        String trailerLink,
        @Nullable
        String photoLink,
        @Nullable
        List<String>directors,
        @Nullable
        List<String> cast,
        @Nullable
        List<String> genres) {
}
