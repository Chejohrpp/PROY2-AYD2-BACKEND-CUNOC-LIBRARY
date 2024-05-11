package com.hrp.libreriacunocbackend.repository.books;

import com.hrp.libreriacunocbackend.entities.book.Editorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EditorialRepository extends JpaRepository<Editorial, String> {
    @Override
    Optional<Editorial> findById(String id);

    Optional<Editorial> findByName(String name);

    @Query(value = "SELECT * FROM editorial WHERE name LIKE LOWER(CONCAT('%', :filter, '%')) ORDER BY id_editorial ASC", nativeQuery = true)
    List<Editorial> getEditorialsNameByFilter(@Param("filter") String filter);
}
