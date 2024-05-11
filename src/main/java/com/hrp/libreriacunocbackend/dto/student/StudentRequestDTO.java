package com.hrp.libreriacunocbackend.dto.student;

import com.hrp.libreriacunocbackend.enums.user.Role;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
public class StudentRequestDTO {
    String username;
//    String password;
    String name;
    String carnet;
    LocalDateTime birth;
    Long idCareer;
}
