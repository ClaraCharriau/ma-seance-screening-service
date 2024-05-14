package com.maseance.screening.service.repository;

import com.maseance.screening.service.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {

    @Query(value = "SELECT movie.* FROM movie INNER JOIN screening ON movie.id_movie = screening.id_movie " +
            "WHERE screening.id_theater = :theaterId", nativeQuery = true)
    List<Movie> getMoviesByTheaterId(@Param("theaterId") UUID theaterId);
}
