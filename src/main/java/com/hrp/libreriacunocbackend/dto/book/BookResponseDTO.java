package com.hrp.libreriacunocbackend.dto.book;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class BookResponseDTO {

    String isbn;
    String author;
    String title;
    LocalDateTime datePublish;
    String editorial;
    Integer amount;

}
