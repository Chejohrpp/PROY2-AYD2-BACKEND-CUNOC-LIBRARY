package com.hrp.libreriacunocbackend.repository;

import com.hrp.libreriacunocbackend.entities.Borrow;
import com.hrp.libreriacunocbackend.entities.user.Student;
import com.hrp.libreriacunocbackend.enums.borrow.State;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    int countByStudentAndState(Student student, State state);
}
