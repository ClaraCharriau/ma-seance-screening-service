package com.maseance.screening.service.client;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TMDBClient {
    private final String TMDB_PATH = "https://api.themoviedb.org/3";
    private final String REGION_CODE = "FR";
    private final String LANGUAGE_CODE = "fr-FR";

    @Value("${TMDB_API_KEY}")
    private String TMDB_API_KEY;

    OkHttpClient TMDB_CLIENT = new OkHttpClient();

    public ResponseBody getCurrentlyPlaying() throws IOException {
        Request request = buildGetRequest(TMDB_PATH + "/movie/now_playing?language=" + LANGUAGE_CODE
                + "&page=1&region=" + REGION_CODE);

        Response response = buildResponse(request);
        return response.body();
    }

    public ResponseBody getMovieDetails(String tmdbMovieId) throws IOException {
        Request request = buildGetRequest(TMDB_PATH + "/movie/" + tmdbMovieId +
                "?append_to_response=credits%2Cvideos&language=" + LANGUAGE_CODE);

        Response response = buildResponse(request);
        return response.body();
    }

    public ResponseBody searchTMDBMovies(String query) throws IOException {
        Request request = buildGetRequest(TMDB_PATH + "/search/movie?query=" + query
                + "&include_adult=false&language=" + LANGUAGE_CODE + "&page=1");

        Response response = buildResponse(request);
        return response.body();
    }

    private Request buildGetRequest(String url) {
        return new Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer " + TMDB_API_KEY)
                .build();
    }

    private Response buildResponse(Request request) throws IOException {
        return TMDB_CLIENT.newCall(request).execute();
    }
}
