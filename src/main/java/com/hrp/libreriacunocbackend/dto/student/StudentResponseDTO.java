package com.hrp.libreriacunocbackend.dto.student;

import com.hrp.libreriacunocbackend.entities.user.Student;
import lombok.Value;

import java.time.LocalDate;

@Value
public class StudentResponseDTO {
    String username;
    String carnet;
    String name;
    LocalDate birth;
    String careerName;

    public StudentResponseDTO(Student studentd){
        this.username = studentd.getUser().getUsername();
        this.carnet = studentd.getCarnet();
        this.name = studentd.getName();
        this.birth = studentd.getBirth();
        this.careerName = studentd.getCareer().getName();
    }
}
