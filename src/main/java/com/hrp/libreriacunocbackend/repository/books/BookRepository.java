package com.hrp.libreriacunocbackend.repository.books;

import com.hrp.libreriacunocbackend.entities.book.Book;
import com.hrp.libreriacunocbackend.entities.user.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    @Query(value = "SELECT * FROM book ORDER BY id_book ASC LIMIT :startIndex, :endIndex", nativeQuery = true)
    List<Book> findByIndexRange(@Param("startIndex") int startIndex, @Param("endIndex") int endIndex);

    @Override
    Optional<Book> findById(Long aLong);

    Optional<Book> findByIsbn(String isbn);
}
