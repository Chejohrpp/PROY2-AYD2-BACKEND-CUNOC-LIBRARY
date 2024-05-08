package com.hrp.libreriacunocbackend.entities.book;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Editorial {
    @Id
    @Column(name = "id_editorial", nullable = false)
    private String idEditorial;

    @Column(name = "name")
    private String name;
}
