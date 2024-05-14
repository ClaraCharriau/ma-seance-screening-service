package com.maseance.screening.service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maseance.screening.service.client.TMDBClient;
import com.maseance.screening.service.dto.MovieDto;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class MovieService {
    @Autowired
    private TMDBClient tmdbClient;
    private final String YOUTUBE_PATH = "https://www.youtube.com/watch?v=";
    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public MovieDto getMovie(boolean extendedInfos, String tmdbMovieId) throws IOException {
        JsonNode tmdbMovieDetails = getTMDBMovieDetails(tmdbMovieId);

        if (extendedInfos) {
            return buildDetailedMovieDto(tmdbMovieDetails);
        }
        return buildMovieDto(tmdbMovieDetails);
    }

    public List<MovieDto> getCurrentlyPlayingMovies(boolean extendedInfos) throws IOException {
        List<JsonNode> tmdbMovieList = getTMDBCurrentlyPlayingMovies();

        if (extendedInfos) {
            return buildDetailedMovieList(tmdbMovieList);
        }
        return buildMovieList(tmdbMovieList);
    }

    private JsonNode getTMDBMovieDetails(String tmdbMovieId) throws IOException {
        ResponseBody tmdbResponseBody;
        try {
            tmdbResponseBody = tmdbClient.getMovieDetails(tmdbMovieId);
        } catch (Exception e) {
            throw new ResponseStatusException(NOT_FOUND, "Could not get TMDB movie details for id : " + tmdbMovieId);
        }
        return OBJECT_MAPPER.readValue(tmdbResponseBody.string(), JsonNode.class);
    }

    private List<JsonNode> getTMDBCurrentlyPlayingMovies() throws IOException {
        ResponseBody tmdbResponseBody;
        try {
            tmdbResponseBody = tmdbClient.getCurrentlyPlaying();
        } catch (Exception e) {
            throw new ResponseStatusException(NOT_FOUND, "Could not get TMDB currently playing movies");
        }
        JsonNode tmdbResponseNode = OBJECT_MAPPER.readValue(tmdbResponseBody.string(), JsonNode.class);

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
                                          String JobName) {
        int CREDITS_NAME_LIMIT = 5;

        return StreamSupport.stream(movieNode.get("credits").get(creditCategory).spliterator(), false)
                .limit(CREDITS_NAME_LIMIT)
                .filter(memberNode -> memberNode != null &&
                        memberNode.get(filterName).asText().equals(JobName))
                .map(memberNode -> memberNode.get("name").asText())
                .toList();
    }

    private MovieDto buildMovieDto(JsonNode movieNode) {
        return MovieDto.builder()
                .id(movieNode.get("id").asText())
                .title(movieNode.get("title").asText())
                .posterLink(movieNode.get("poster_path").asText())
                .build();
    }

    private MovieDto buildDetailedMovieDto(JsonNode movieNode) {
        return MovieDto.builder()
                .id(movieNode.get("id").asText())
                .title(movieNode.get("title").asText())
                .posterLink(movieNode.get("poster_path").asText())
                .resume(movieNode.get("overview").asText())
                .releaseDate(movieNode.get("release_date").asText())
                .duration(movieNode.get("runtime").asInt())
                .photoLink(movieNode.get("backdrop_path").asText())
                .trailerLink(getTrailerLink(movieNode))
                .cast(getCreditMembers(movieNode, "cast", "known_for_department", "Acting"))
                .directors(getCreditMembers(movieNode, "crew", "job", "Director"))
                .genres(movieNode.get("genres").findValuesAsText("name"))
                .build();
    }

    private List<MovieDto> buildMovieList(List<JsonNode> jsonNodeMovieList) {
        return jsonNodeMovieList.stream()
                .map(this::buildMovieDto)
                .toList();
    }

    private List<MovieDto> buildDetailedMovieList(List<JsonNode> jsonNodeMovieList) throws IOException {
        return getTMDBMovieDetailsList(jsonNodeMovieList).stream()
                .map(this::buildDetailedMovieDto)
                .toList();
    }
}
