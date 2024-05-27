package com.maseance.screening.service.repository;

import com.maseance.screening.service.model.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, UUID> {
    @Query(value = "SELECT * FROM Theater " +
            "WHERE LOWER(name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(address) LIKE LOWER(CONCAT('%', :address, '%'))", nativeQuery = true)
    List<Theater> findByNameOrAddressContainingIgnoreCase(@Param("name") String name, @Param("address") String address);
    Theater getTheaterByName(String name);
    boolean existsByName(String name);
}
