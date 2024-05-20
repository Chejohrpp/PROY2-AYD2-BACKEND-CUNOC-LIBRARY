package com.hrp.libreriacunocbackend.entities;

import com.hrp.libreriacunocbackend.entities.book.Book;
import com.hrp.libreriacunocbackend.entities.user.Student;
import com.hrp.libreriacunocbackend.enums.borrow.State;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Borrow {

    @Id
    @Column(name = "id_borrow")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBorrow;

    @Column(name = "date_borrow")
    private LocalDate dateBorrow;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(name = "price_to_borrow")
    private Double priceBorrow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_user_id_user")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id_book")
    private Book book;

}
