package com.maseance.screening.service.repository;

import com.maseance.screening.service.model.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, String> {
}
