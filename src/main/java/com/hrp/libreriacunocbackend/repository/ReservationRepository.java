package com.hrp.libreriacunocbackend.repository;

import com.hrp.libreriacunocbackend.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
