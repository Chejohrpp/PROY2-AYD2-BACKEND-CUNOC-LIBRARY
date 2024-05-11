package com.hrp.libreriacunocbackend.dto.career;

import com.hrp.libreriacunocbackend.entities.user.Career;
import lombok.Value;

@Value
public class CareerResponseDTO {
    Long idCareer;
    String name;

    public CareerResponseDTO(Career career) {
        this.idCareer = career.getIdCareer();
        this.name = career.getName();
    }
}
