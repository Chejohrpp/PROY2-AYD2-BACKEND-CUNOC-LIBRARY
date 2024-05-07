package com.hrp.libreriacunocbackend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Book {
    @Id
    @Column(name = "id_book", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBook;

    @Column(name = "title")
    private String title;


}
