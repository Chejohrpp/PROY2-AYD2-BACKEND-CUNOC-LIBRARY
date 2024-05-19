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

    @Query(value = "SELECT * FROM book b WHERE b.amount = 0", nativeQuery = true)
    List<Book> findBooksOutOfStock();

    @Query(value = "SELECT DISTINCT b.* FROM book b LEFT JOIN borrow borrows ON b.id_book = borrows.book_id_book WHERE borrows.id_borrow IS NULL", nativeQuery = true)
    List<Book> findBooksNeverBorrowed();
}
