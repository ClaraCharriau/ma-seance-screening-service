package com.maseance.screening.service.scrapper;

import java.io.IOException;
import java.util.List;

import com.maseance.screening.service.scrapper.response.GoogleShowtimesResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * GoogleShowtimesScrapper
 * Get showtimes infos from Google Search
 */
public class GoogleShowtimesScrapper {
    private static final String GOOGLE_SEARCH_PATH = "https://www.google.com/search?q=";

    public static GoogleShowtimesResponse getGoogleShowtimesByTheaterName(String theaterQuery) throws IOException {
        // GET request and parse result
        Document googleSearchResultPage = Jsoup.connect(GOOGLE_SEARCH_PATH + theaterQuery).get();
        Elements showtimesDOMElements = googleSearchResultPage.select(".lr_c_fcb.lr-s-stor");

        return new GoogleShowtimesResponse(getTheaterName(googleSearchResultPage),
                getMovieShowtimes(showtimesDOMElements));
    }

    private static List<GoogleShowtimesResponse.MovieShowtimes> getMovieShowtimes(Elements showtimesElements) {
        return showtimesElements.stream().map(GoogleShowtimesScrapper::buildMovieShowtimes).toList();
    }

    private static GoogleShowtimesResponse.MovieShowtimes buildMovieShowtimes(Element showtimeElement) {
        return new GoogleShowtimesResponse.MovieShowtimes(getMovieName(showtimeElement),
                getDay(showtimeElement),
                getMovieSessions(showtimeElement));
    }

    private static List<String> getMovieSessions(Element showtimeElement) {
        return showtimeElement.select(".lr_c_s .lr_c_fce").stream()
                .map(sessionElement -> sessionElement.select(".std-ts").text().trim()).toList();
    }

    private static String getDay(Element showtimeElement) {
        return showtimeElement.parent().attr("data-date");
    }

    private static String getMovieName(Element showtimeElement) {
        return showtimeElement.select(".vk_bk.lr-s-din").text().trim();
    }

    private static String getTheaterName(Document googleSearchResultPage) {
        return googleSearchResultPage.select(".WIDPrb").attr("data-theater-name");
    }
}
