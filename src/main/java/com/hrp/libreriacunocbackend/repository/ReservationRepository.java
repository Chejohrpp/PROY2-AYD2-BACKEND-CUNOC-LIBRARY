package com.hrp.libreriacunocbackend.repository;

import com.hrp.libreriacunocbackend.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByDateBeforeAndIsActivate(LocalDate yesterday, Boolean activate);

    @Query(value = "SELECT * FROM reservation ORDER BY id_reservation ASC LIMIT :startIndex, :endIndex", nativeQuery = true)
    List<Reservation> findByRange(@Param("startIndex") Integer startIndex, @Param("endIndex") Integer endIndex);
}
