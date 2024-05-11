package com.hrp.libreriacunocbackend.repository.career;

import com.hrp.libreriacunocbackend.entities.user.Career;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CareerRepository extends JpaRepository<Career, Long> {

    Optional<Career> findByName (String name);

    @Query(value = "SELECT * FROM career WHERE name LIKE LOWER(CONCAT('%', :filter, '%')) ORDER BY id_career ASC", nativeQuery = true)
    List<Career> getByNameFilter(@Param("filter") String filter);
}
