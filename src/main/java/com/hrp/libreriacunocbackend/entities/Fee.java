package com.hrp.libreriacunocbackend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Fee {

    @Id
    @Column(name = "borrow_id_borrow")
    private Long idBorrow;

    @OneToOne
    @MapsId
    @JoinColumn(name = "borrow_id_borrow")
    private Borrow borrow;

    private LocalDate date;

    private Double price;
}
