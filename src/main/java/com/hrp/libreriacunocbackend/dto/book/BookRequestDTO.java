package com.hrp.libreriacunocbackend.dto.book;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class BookRequestDTO {
    String isbn;
    String idAuthor;
    String title;
    LocalDateTime datePublish;
    String idEditorial;

}
