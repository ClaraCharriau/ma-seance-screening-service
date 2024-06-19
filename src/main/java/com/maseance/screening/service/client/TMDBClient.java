package com.maseance.screening.service.client;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;

@Service
public class TMDBClient {
    private static final String TMDB_PATH = "https://api.themoviedb.org/3";
    private static final String REGION_CODE = "FR";
    private static final String LANGUAGE_CODE = "fr-FR";

    @Value("${TMDB_API_KEY}")
    private String TMDB_API_KEY;

    OkHttpClient tmdbClient = new OkHttpClient();

    public ResponseBody getCurrentlyPlaying() {
        return executeRequest("/movie/now_playing?language=" + LANGUAGE_CODE
                + "&page=1&region=" + REGION_CODE);
    }

    public ResponseBody getMovieDetails(String tmdbMovieId) {
        return executeRequest("/movie/" + tmdbMovieId +
                "?append_to_response=credits%2Cvideos&language=" + LANGUAGE_CODE);
    }

    public ResponseBody searchTMDBMovies(String query) {
        return executeRequest("/search/movie?query=" + query
                + "&include_adult=false&language=" + LANGUAGE_CODE + "&page=1");
    }

    private ResponseBody executeRequest(String endpoint) {
        Request request = buildGetRequest(TMDB_PATH + endpoint);

        try {
            Response response = tmdbClient.newCall(request).execute();
            return response.body();
        } catch (IOException e) {
            var message = "Could not get response from TMDB with request GET : " + endpoint;
            throw new ResponseStatusException(NOT_FOUND, message, e);
        }
    }

    private Request buildGetRequest(String url) {
        return new Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer " + TMDB_API_KEY)
                .build();
    }
}
