package com.hrp.libreriacunocbackend.service.career;

import com.hrp.libreriacunocbackend.dto.career.CareerRequestDTO;
import com.hrp.libreriacunocbackend.dto.career.CareerResponseDTO;
import com.hrp.libreriacunocbackend.entities.user.Career;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import org.aspectj.weaver.ast.Not;

import java.util.List;
import java.util.Optional;

public interface CareerService {

    CareerResponseDTO createCareer(CareerRequestDTO careerRequestDTO) throws NotAcceptableException;

    Optional<Career> findCarrerById(Long id);

    List<CareerResponseDTO> getByFilter(String filter) throws BadRequestException;
}
