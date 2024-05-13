package com.maseance.screening.service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maseance.screening.service.client.TMDBClient;
import com.maseance.screening.service.dto.MovieDto;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class MovieService {
    @Autowired
    private TMDBClient tmdbClient;
    private final String YOUTUBE_PATH = "https://www.youtube.com/watch?v=";
    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public List<MovieDto> getCurrentlyPlayingMovies(boolean extendedInfos) throws IOException {
        ResponseBody tmdbResponseBody = tmdbClient.getCurrentlyPlaying();
        JsonNode tmdbResponseNode = OBJECT_MAPPER.readValue(tmdbResponseBody.string(), JsonNode.class);

        List<JsonNode> tmdbMovieList = StreamSupport
                .stream(tmdbResponseNode.get("results").spliterator(), false)
                .toList();

        if (extendedInfos) {
            return buildDetailedMovieList(tmdbMovieList);
        }
        return buildMovieList(tmdbMovieList);
    }

    private JsonNode getTMDBMovieDetails(String tmdbMovieId) throws IOException {
        ResponseBody tmdbResponseBody = tmdbClient.getMovieDetails(tmdbMovieId);
        return OBJECT_MAPPER.readValue(tmdbResponseBody.string(), JsonNode.class);
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

    private List<MovieDto> buildMovieList(List<JsonNode> jsonNodeMovieList) {
        return jsonNodeMovieList.stream()
                .map(movieNode -> MovieDto.builder()
                        .id(movieNode.get("id").asText())
                        .title(movieNode.get("title").asText())
                        .posterLink(movieNode.get("poster_path").asText())
                        .build())
                .toList();
    }

    private List<MovieDto> buildDetailedMovieList(List<JsonNode> jsonNodeMovieList) throws IOException {
        List<JsonNode> movieList = getTMDBMovieDetailsList(jsonNodeMovieList);

        return movieList.stream()
                .map(movieNode -> MovieDto.builder()
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
                        .build()).toList();
    }
}
