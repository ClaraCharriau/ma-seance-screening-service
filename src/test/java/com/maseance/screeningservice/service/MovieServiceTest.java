package com.maseance.screeningservice.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.maseance.screening.service.client.TMDBClient;
import com.maseance.screening.service.dto.MovieDto;
import com.maseance.screening.service.model.Movie;
import com.maseance.screening.service.repository.MovieRepository;
import com.maseance.screening.service.service.MovieService;
import com.maseance.screeningservice.utils.ResourceUtils;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private TMDBClient tmdbClient;
    @InjectMocks
    private MovieService movieService;

    @Test
    void should_successfully_get_movie_by_id() throws IOException {
        // Given
        var movieId = UUID.fromString("bad636e8-2393-4949-a5ca-3ef42c7351e6");
        var movieEntity = Movie.builder()
                .id(movieId)
                .tmdbId("tmdbId")
                .build();
        given(movieRepository.existsById(movieId)).willReturn(true);
        given(movieRepository.getReferenceById(movieId)).willReturn(movieEntity);
        given(tmdbClient.getMovieDetails(movieEntity.getTmdbId()))
                .willReturn(ResponseBody.create(buildTMDBGetMovieResponse(), MediaType.get("application/json")));

        // When
        var movieDto = movieService.getMovieDtoById(false, movieId);

        // Then
        assertThat(movieDto).extracting(MovieDto::id).isEqualTo(movieId.toString());
    }

    @Test
    void should_throw_exception_when_movie_does_not_exists_in_database() {
        // Given
        var movieId = UUID.fromString("bad636e8-2393-4949-a5ca-3ef42c7351e6");
        given(movieRepository.existsById(movieId)).willReturn(false);

        // When
        var throwable = catchThrowable(() -> movieService.getMovieDtoById(false, movieId));

        // Then
        assertThat(throwable).isNotNull().isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Could not find movie by id : bad636e8-2393-4949-a5ca-3ef42c7351e6")
                .hasMessageContaining("404 NOT_FOUND");
    }

    @Test
    void should_return_currently_playing_movies() throws IOException {
        // Given
        var movieId = UUID.fromString("bad636e8-2393-4949-a5ca-3ef42c7351e6");
        var movieEntity = Movie.builder()
                .id(movieId)
                .tmdbId("tmdbId")
                .build();
        given(movieRepository.findByTmdbId(any(String.class))).willReturn(List.of(movieEntity));
        given(tmdbClient.getCurrentlyPlaying())
                .willReturn(ResponseBody.create(buildTMDBNowPlayingResponse(), MediaType.get("application/json")));

        // When
        var movieDtos = movieService.getCurrentlyPlayingMovies(false);

        // Then
        assertThat(movieDtos).isNotEmpty();
        assertThat(movieDtos.get(0).title()).isEqualTo("Kingdom of the Planet of the Apes");
        assertThat(movieDtos.get(1).title()).isEqualTo("Bad Boys: Ride or Die");
        assertThat(movieDtos.get(2).title()).isEqualTo("Inside Out 2");
    }

    private static String buildTMDBGetMovieResponse() throws IOException {
        return ResourceUtils.asString("/mock/tmdb-get-movie-by-id-response.json");
    }

    private static String buildTMDBNowPlayingResponse() throws IOException {
        return ResourceUtils.asString("/mock/tmdb-now-playing-response.json");
    }
}