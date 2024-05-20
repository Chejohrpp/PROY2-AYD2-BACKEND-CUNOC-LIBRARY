package com.hrp.libreriacunocbackend.dto.student;

import com.hrp.libreriacunocbackend.entities.user.Student;
import lombok.Value;

import java.time.LocalDate;

@Value
public class StudentResponseDTO {
    Long id;
    String username;
    String carnet;
    String name;
    LocalDate birth;
    String careerName;
    Boolean canBorrow;

    public StudentResponseDTO(Student studentd){
        this.id = studentd.getUser().getIdUser();
        this.username = studentd.getUser().getUsername();
        this.carnet = studentd.getCarnet();
        this.name = studentd.getName();
        this.birth = studentd.getBirth();
        this.careerName = studentd.getCareer().getName();
        this.canBorrow = studentd.getCanBorrow();
    }
}
