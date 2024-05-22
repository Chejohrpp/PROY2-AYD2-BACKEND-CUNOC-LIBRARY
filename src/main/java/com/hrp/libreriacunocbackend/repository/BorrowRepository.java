package com.hrp.libreriacunocbackend.repository;

import com.hrp.libreriacunocbackend.entities.Borrow;
import com.hrp.libreriacunocbackend.entities.user.Student;
import com.hrp.libreriacunocbackend.enums.borrow.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    int countByStudentAndState(Student student, State state);

    @Query(value = "SELECT * FROM borrow b WHERE :date IS NULL OR b.date_borrow = :date", nativeQuery = true)
    List<Borrow> findBorrowsToBeReturnedToday(@Param("date") LocalDate date);

    @Query(value = "SELECT * FROM borrow b WHERE b.date_borrow = :date", nativeQuery = true)
    List<Borrow> findBorrowsToBeReturnedOnDate(@Param("date") LocalDate date);

    @Query(value = "SELECT * FROM borrow b WHERE b.state = 'LATE' OR b.state = 'LOST'", nativeQuery = true)
    List<Borrow> findLateBorrows();

    @Query(value = "SELECT SUM(f.late_fee + f.fee) FROM borrow b INNER JOIN fee f ON b.id_borrow = f.borrow_id_borrow WHERE f.date BETWEEN :startDate AND :endDate AND b.state = 'RETURN'", nativeQuery = true)
    Double findTotalRevenueInInterval(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT * FROM borrow b WHERE b.date_borrow BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<Borrow> findBorrowsInInterval(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT COUNT(*) FROM borrow b INNER JOIN student s ON b.student_user_id_user = s.user_id_user WHERE s.career_id_career = :careerId AND b.date_borrow BETWEEN :startDate AND :endDate", nativeQuery = true)
    int countBorrowsByCareerInInterval(@Param("careerId") Long careerId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT * FROM borrow b INNER JOIN student s ON b.student_user_id_user = s.user_id_user WHERE s.career_id_career = :careerId AND b.date_borrow BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<Borrow> findBorrowsByCareerInInterval(@Param("careerId") Long careerId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT * FROM borrow b WHERE b.student_user_id_user = :studentId AND (state = 'LOST' OR state = 'LATE' OR state='RETURN') AND b.date_borrow BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<Borrow> findLateBorrowsByStudentAndInterval(@Param("studentId") Long studentId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT COUNT(*) FROM borrow b WHERE b.student_user_id_user = :studentId AND b.date_borrow BETWEEN :startDate AND :endDate", nativeQuery = true)
    int countBorrowsByStudentInInterval(@Param("studentId") Long studentId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT * FROM borrow b WHERE b.student_user_id_user = :studentId AND b.date_borrow BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<Borrow> findBorrowsByStudentAndInterval(@Param("studentId") Long studentId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT * FROM borrow b WHERE b.student.userId = :studentId AND b.state = 'BORROW'", nativeQuery = true)
    List<Borrow> findActiveBorrowsByStudent(@Param("studentId") Long studentId);

    @Query(value = "SELECT * FROM borrow WHERE student_user_id_user = :studentId AND (state = 'BORROW' OR state = 'LOST' OR state = 'LATE')", nativeQuery = true)
    List<Borrow> findCurrentBorrowsByStudent(@Param("studentId") Long studentId);

}
