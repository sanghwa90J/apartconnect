package com.aptconnect.repository;

import com.aptconnect.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
    boolean existsByName(String name);

    List<Apartment> findByNameContaining(String search);

    @Query("SELECT a.name, COUNT(a) FROM Apartment a GROUP BY a.name")
    List<Object[]> countApartmentByName();

    @Query("SELECT FUNCTION('MONTH', a.createDate), COUNT(a) FROM Apartment a " +
            "WHERE a.createDate >= :startDate GROUP BY FUNCTION('MONTH', a.createDate)")
    List<Object[]> countMonthlyApartmentRegistrations(@Param("startDate") LocalDateTime startDate);

    List<Apartment> findByNameContainingIgnoreCase(String name);
}


