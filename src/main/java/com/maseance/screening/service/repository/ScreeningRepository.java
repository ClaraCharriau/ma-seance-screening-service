package com.maseance.screening.service.repository;

import com.maseance.screening.service.model.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScreeningRepository extends JpaRepository<Screening, UUID> {
    List<Screening> findByTheaterIdAndDateBetween(UUID theaterId, LocalDateTime startDate, LocalDateTime endDate);
    List<Screening> findByMovieIdAndDateBetween(UUID movieId, LocalDateTime startDate, LocalDateTime endDate);
    void deleteByTheaterIdAndDateBetween(UUID theaterId, LocalDateTime startDate, LocalDateTime endDate);
}
