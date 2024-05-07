package com.hrp.libreriacunocbackend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Career {

    @Id
    @Column(name = "id_career")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCareer;

    @Column(name = "name")
    private String name;

}
