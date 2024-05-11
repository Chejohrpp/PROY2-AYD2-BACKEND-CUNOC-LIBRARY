package com.hrp.libreriacunocbackend.repository.books;

import com.hrp.libreriacunocbackend.entities.book.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, String> {

    @Override
    Optional<Author> findById(String id);

    Optional<Author> findByName(String name);

    @Query(value = "SELECT * FROM author WHERE name LIKE LOWER(CONCAT('%', :filter, '%')) ORDER BY id_author ASC", nativeQuery = true)
    List<Author> getAuthorsByFilter(@Param("filter") String filter);
}
