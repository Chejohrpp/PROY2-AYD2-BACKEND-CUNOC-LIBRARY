package com.hrp.libreriacunocbackend.repository;

import com.hrp.libreriacunocbackend.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByDateBeforeAndIsActivate(LocalDate yesterday, Boolean activate);
}
