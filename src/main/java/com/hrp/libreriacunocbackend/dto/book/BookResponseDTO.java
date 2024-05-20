package com.hrp.libreriacunocbackend.dto.book;

import com.hrp.libreriacunocbackend.entities.book.Book;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
public class BookResponseDTO {

    Long id;
    String isbn;
    String author;
    String title;
    LocalDate datePublish;
    String editorial;
    Integer amount;
    Double price;

    public BookResponseDTO(Book book){
        this.id = book.getIdBook();
        this.isbn = book.getIsbn();
        this.author = book.getAuthor().getName();
        this.title = book.getTitle();
        this.datePublish = book.getDatePublish();
        this.editorial = book.getEditorial().getName();
        this.amount = book.getAmount();
        this.price = book.getPrice();
    }

}
