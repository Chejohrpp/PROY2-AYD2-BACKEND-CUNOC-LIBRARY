package com.hrp.libreriacunocbackend.entities.user;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Student {

    @Id
    @Column(name = "user_id_user")
    private Long userId;

    @Column(name = "name")
    private String name;

    @Column(name= "birth")
    private LocalDate birth;

    @Column(name= "carnet")
    private String carnet;

    @Column(name= "can_borrow")
    private Boolean canBorrow = true;

    @ManyToOne
    @JoinColumn(name = "career_id_career")
    private Career career;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id_user")
    private User user;

}
