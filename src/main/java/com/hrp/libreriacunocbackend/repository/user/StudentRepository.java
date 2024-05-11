package com.hrp.libreriacunocbackend.repository.user;

import com.hrp.libreriacunocbackend.entities.user.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByCarnet(String carnet);
}
