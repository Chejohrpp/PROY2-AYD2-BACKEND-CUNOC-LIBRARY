package com.hrp.libreriacunocbackend.repository;

import com.hrp.libreriacunocbackend.entities.Fee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FeeRepository extends JpaRepository<Fee, Long> {

    @Query(value = "SELECT f FROM Fee f WHERE f.borrow.student.userId = :studentId AND f.date BETWEEN :startDate AND :endDate AND f.lateFee > 0")
    List<Fee> findLateFeesByStudentAndInterval(@Param("studentId") Long studentId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
