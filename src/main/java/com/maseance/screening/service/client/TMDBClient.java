package com.maseance.screening.service.client;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class TMDBClient {
    private final String TMDB_PATH = "https://api.themoviedb.org/3/";
    private final String REGION_CODE = "FR";
    private final String LANGUAGE_CODE = "fr-FR";

    @Value("${TMDB_API_KEY}")
    private String TMDB_API_KEY;

    OkHttpClient TMDB_CLIENT = new OkHttpClient();

    public ResponseBody getCurrentlyPlaying() throws IOException {
        Request request = new Request.Builder()
                .url(TMDB_PATH + "movie/now_playing?language=" + LANGUAGE_CODE + "&page=1&region=" + REGION_CODE)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer " + TMDB_API_KEY)
                .build();

        Response response = TMDB_CLIENT.newCall(request).execute();
        return response.body();
    }

    public ResponseBody getMovieDetails(String tmdbMovieId) throws IOException {
        Request request = new Request.Builder()
                .url(TMDB_PATH + "movie/" + tmdbMovieId + "?append_to_response=credits%2Cvideos&language=" + LANGUAGE_CODE)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer " + TMDB_API_KEY)
                .build();

        Response response = TMDB_CLIENT.newCall(request).execute();
        return response.body();
    }
}
