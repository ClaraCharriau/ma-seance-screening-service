package com.maseance.screening.service.repository;

import com.maseance.screening.service.model.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, UUID> {
    List<Theater> findByNameOrAddressContainingIgnoreCase(String name, String address);
}
