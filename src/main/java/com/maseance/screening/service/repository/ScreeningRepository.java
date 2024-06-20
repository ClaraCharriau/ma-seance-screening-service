package com.maseance.screening.service.repository;

import com.maseance.screening.service.model.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScreeningRepository extends JpaRepository<Screening, UUID> {
    List<Screening> findByTheaterIdAndDateBetween(UUID theaterId, LocalDateTime startDate, LocalDateTime endDate);

    void deleteByTheaterIdAndDateBetween(UUID theaterId, LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "SELECT s.* " +
            "FROM screening s " +
            "JOIN theater t ON s.id_theater = t.id_theater " +
            "JOIN theater_bookmark tb ON t.id_theater = tb.id_theater " +
            "WHERE s.id_movie = :movieId " +
            "AND s.date BETWEEN :startDate AND :endDate " +
            "AND tb.id_user = :userId", nativeQuery = true)
    List<Screening> findScreeningsByMovieIdAndDateRangeAndUserId(
            @Param("movieId") UUID movieId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("userId") UUID userId);
}
