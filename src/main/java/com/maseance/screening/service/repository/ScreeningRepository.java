package com.maseance.screening.service.repository;

import com.maseance.screening.service.model.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScreeningRepository extends JpaRepository<Screening, UUID> {
    List<Screening> getByTheaterId(UUID theaterId);
}
