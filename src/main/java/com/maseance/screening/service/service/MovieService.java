package com.maseance.screening.service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maseance.screening.service.client.TMDBClient;
import com.maseance.screening.service.dto.MovieDto;
import com.maseance.screening.service.model.Movie;
import com.maseance.screening.service.repository.MovieRepository;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Slf4j
public class MovieService {
    private static final String YOUTUBE_PATH = "https://www.youtube.com/watch?v=";
    ObjectMapper objectMapper = new ObjectMapper();
    private TMDBClient tmdbClient;
    private MovieRepository movieRepository;

    public MovieDto getMovieDtoById(boolean extendedInfos, UUID movieId) throws IOException {
        var movieEntity = getMovieById(movieId);
        JsonNode tmdbMovieDetails = getTMDBMovieDetails(movieEntity.getTmdbId());

        return buildMovieDto(tmdbMovieDetails, movieId, extendedInfos);
    }

    public MovieDto getMovieDtoByTmdbId(boolean extendedInfos, String tmdbMovieId) throws IOException {
        JsonNode tmdbMovieDetails = getTMDBMovieDetails(tmdbMovieId);
        var movieId = getMovieIdByTmdbId(tmdbMovieDetails.get("id").asText());

        return buildMovieDto(tmdbMovieDetails, movieId, extendedInfos);
    }

    public List<MovieDto> getMoviesByTmdbId(List<Movie> movieEntities) throws IOException {
        var tmdbIdList = movieEntities.stream()
                .map(Movie::getTmdbId)
                .toList();

        List<MovieDto> movieDtos = new ArrayList<>();
        for (var id : tmdbIdList) {
            movieDtos.add(getMovieDtoByTmdbId(false, id));
        }
        return movieDtos;
    }

    public List<MovieDto> getCurrentlyPlayingMovies(boolean extendedInfos) throws IOException {
        List<JsonNode> tmdbMovieList = getTMDBCurrentlyPlayingMovies();

        return buildMovieDtoList(tmdbMovieList, extendedInfos);
    }

    public List<MovieDto> searchMovies(String query) throws IOException {
        List<JsonNode> tmdbMovieList = searchTMDBMovies(query);

        return buildDetailedMovieDtoList(tmdbMovieList);
    }

    public Movie findMovieEntityByName(String movieName) throws IOException {
        var firstTMDBMovieResult = searchTMDBMovies(movieName).stream().findFirst()
                .orElse(null);

        if (firstTMDBMovieResult != null) {
            return getMovieDtoByTmdbId(firstTMDBMovieResult.get("id").asText());
        }
        log.warn("Could not find movie in TMDB with name : " + movieName);
        return null;
    }

    private Movie getMovieById(UUID movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new ResponseStatusException(NOT_FOUND, "Could not find movie by id : " + movieId);
        }
        return movieRepository.getReferenceById(movieId);
    }

    private JsonNode getTMDBMovieDetails(String tmdbMovieId) throws IOException {
        ResponseBody tmdbResponseBody = tmdbClient.getMovieDetails(tmdbMovieId);
        return objectMapper.readValue(tmdbResponseBody.string(), JsonNode.class);
    }

    private List<JsonNode> getTMDBCurrentlyPlayingMovies() throws IOException {
        ResponseBody tmdbResponseBody = tmdbClient.getCurrentlyPlaying();
        JsonNode tmdbResponseNode = objectMapper.readValue(tmdbResponseBody.string(), JsonNode.class);

        return StreamSupport
                .stream(tmdbResponseNode.get("results").spliterator(), false)
                .toList();
    }

    private List<JsonNode> searchTMDBMovies(String query) throws IOException {
        ResponseBody tmdbResponseBody = tmdbClient.searchTMDBMovies(query);
        JsonNode tmdbResponseNode = objectMapper.readValue(tmdbResponseBody.string(), JsonNode.class);

        return StreamSupport
                .stream(tmdbResponseNode.get("results").spliterator(), false)
                .toList();
    }


    private List<JsonNode> getTMDBMovieDetailsList(List<JsonNode> jsonNodeMovieList) throws IOException {
        var tmdbMovieIdList = jsonNodeMovieList.stream()
                .map(movieNode -> movieNode.get("id").asText())
                .toList();

        List<JsonNode> movieList = new ArrayList<>();
        for (var tmdbMovieId : tmdbMovieIdList) {
            movieList.add(getTMDBMovieDetails(tmdbMovieId));
        }
        return movieList;
    }

    private UUID getMovieIdByTmdbId(String tmdbId) {
        return getMovieDtoByTmdbId(tmdbId).getId();
    }

    private Movie getMovieDtoByTmdbId(String tmdbId) {
        var movieEntities = movieRepository.findByTmdbId(tmdbId);
        if (movieEntities.isEmpty()) {
            // If movie does not exist in database, we need to create and save it first.
            saveMovie(tmdbId);
            return getMovieDtoByTmdbId(tmdbId);
        }
        return movieEntities.stream().findFirst().get();
    }

    private void saveMovie(String tmdbId) {
        var movie = Movie.builder()
                .id(UUID.randomUUID())
                .tmdbId(tmdbId).build();
        log.info("Inserting new movie in database with id : " + movie.getId());
        movieRepository.save(movie);
    }

    private String getTrailerLink(JsonNode movieNode) {
        JsonNode videosResultsNode = movieNode.path("videos").path("results");

        return StreamSupport.stream(videosResultsNode.spliterator(), false)
                .findFirst()
                .map(video -> YOUTUBE_PATH + video.get("key").asText())
                .orElse("");
    }

    private List<String> getCreditMembers(JsonNode movieNode,
                                          String creditCategory,
                                          String filterName,
                                          String jobName) {
        int creditsNameLimit = 5;

        return StreamSupport.stream(movieNode.get("credits").get(creditCategory).spliterator(), false)
                .filter(memberNode -> memberNode.get(filterName).asText().equals(jobName))
                .map(memberNode -> memberNode.get("name").asText())
                .limit(creditsNameLimit)
                .toList();
    }

    private List<String> getGenres(JsonNode movieNode) {
        return movieNode.get("genres").findValuesAsText("name").stream().limit(3).toList();
    }

    private MovieDto buildMovieDto(JsonNode tmdbMovieDetails, UUID movieId, boolean isDetailed) {
        if (isDetailed) {
            return buildDetailedMovieDto(tmdbMovieDetails, movieId);
        }
        return buildSimpleMovieDto(tmdbMovieDetails, movieId);
    }

    private List<MovieDto> buildMovieDtoList(List<JsonNode> tmdbMovieDetailsList, boolean isDetailed) throws IOException {
        if (isDetailed) {
            return buildDetailedMovieDtoList(tmdbMovieDetailsList);
        }
        return buildSimpleMovieDtoList(tmdbMovieDetailsList);
    }

    private MovieDto buildSimpleMovieDto(JsonNode movieNode, UUID movieId) {
        return MovieDto.builder()
                .id(String.valueOf(movieId))
                .title(movieNode.get("title").asText())
                .posterLink(movieNode.get("poster_path").asText())
                .build();
    }

    private MovieDto buildDetailedMovieDto(JsonNode movieNode, UUID movieId) {
        return MovieDto.builder()
                .id(String.valueOf(movieId))
                .title(movieNode.get("title").asText())
                .posterLink(movieNode.get("poster_path").asText())
                .resume(movieNode.get("overview").asText())
                .releaseDate(movieNode.get("release_date").asText())
                .duration(movieNode.get("runtime").asInt())
                .photoLink(movieNode.get("backdrop_path").asText())
                .trailerLink(getTrailerLink(movieNode))
                .cast(getCreditMembers(movieNode, "cast", "known_for_department", "Acting"))
                .directors(getCreditMembers(movieNode, "crew", "job", "Director"))
                .genres(getGenres(movieNode))
                .build();
    }

    private List<MovieDto> buildSimpleMovieDtoList(List<JsonNode> jsonNodeMovieList) {
        return jsonNodeMovieList.stream()
                .map(movie -> buildSimpleMovieDto(movie,
                        getMovieIdByTmdbId(movie.get("id").asText())))
                .toList();
    }

    private List<MovieDto> buildDetailedMovieDtoList(List<JsonNode> jsonNodeMovieList) throws IOException {
        return getTMDBMovieDetailsList(jsonNodeMovieList).stream()
                .map(movie -> buildDetailedMovieDto(movie,
                        getMovieIdByTmdbId(movie.get("id").asText())))
                .toList();
    }
}
