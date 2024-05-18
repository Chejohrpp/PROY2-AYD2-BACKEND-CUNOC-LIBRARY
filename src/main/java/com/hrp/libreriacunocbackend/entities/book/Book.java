package com.hrp.libreriacunocbackend.entities.book;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Book {
    @Id
    @Column(name = "id_book", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBook;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "title")
    private String title;

    private Integer amount;

    private Double price;

    @Column(name = "date_publish")
    private LocalDate datePublish;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id_author")
    private Author author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editorial_id_editorial")
    private Editorial editorial;



}
