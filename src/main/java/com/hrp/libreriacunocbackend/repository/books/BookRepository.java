package com.hrp.libreriacunocbackend.repository.books;

import com.hrp.libreriacunocbackend.entities.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query(value = "SELECT * FROM book ORDER BY id_book ASC LIMIT :startIndex, :endIndex", nativeQuery = true)
    List<Book> findByIndexRange(@Param("startIndex") int startIndex, @Param("endIndex") int endIndex);

    @Query(value = "SELECT * FROM book WHERE LOWER(:attribute) LIKE LOWER(CONCAT('%', :filter, '%')) ORDER BY id_book ASC", nativeQuery = true)
    List<Book> findByAttribute(@Param("attribute") String attribute, @Param("filter") String filter);
}
