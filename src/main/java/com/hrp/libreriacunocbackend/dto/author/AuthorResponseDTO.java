package com.hrp.libreriacunocbackend.dto.author;

import com.hrp.libreriacunocbackend.entities.book.Author;
import lombok.Value;

@Value
public class AuthorResponseDTO {
    String idAuthor;
    String name;

    public AuthorResponseDTO(Author author) {
        this.idAuthor = author.getIdAuthor();
        this.name = author.getName();
    }

}
