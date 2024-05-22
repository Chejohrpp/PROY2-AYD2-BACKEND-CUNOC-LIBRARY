package com.hrp.libreriacunocbackend.entities;

import com.hrp.libreriacunocbackend.entities.book.Book;
import com.hrp.libreriacunocbackend.entities.user.Student;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Reservation {

    @Id
    @Column(name = "id_reservation")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReservation;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "is_activate")
    private Boolean isActivate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_user_id_user")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id_book")
    private Book book;
}
