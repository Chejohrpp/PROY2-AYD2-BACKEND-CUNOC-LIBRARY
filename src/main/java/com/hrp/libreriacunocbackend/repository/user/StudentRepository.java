package com.hrp.libreriacunocbackend.repository.user;

import com.hrp.libreriacunocbackend.entities.user.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    Optional<Student> findByCarnet(String carnet);

    @Query(value = "SELECT * FROM student WHERE :attribute LIKE LOWER(CONCAT('%', :filter, '%')) ORDER BY user_id_user ASC", nativeQuery = true)
    List<Student> findByAttributeFilter(@Param("attribute") String attribute, @Param("filter") String filter);

    @Query(value = "SELECT * FROM student ORDER BY user_id_user ASC LIMIT :startIndex, :endIndex", nativeQuery = true)
    List<Student> findByRange(@Param("startIndex") Integer startIndex, @Param("endIndex") Integer endIndex);

}
