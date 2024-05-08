package com.hrp.libreriacunocbackend.entities.book;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Author {
    @Id
    @Column(name = "id_author", nullable = false)
    private String idAuthor;

    @Column(name = "name")
    private String name;
}
