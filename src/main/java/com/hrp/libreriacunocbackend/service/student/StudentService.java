package com.hrp.libreriacunocbackend.service.student;

import com.hrp.libreriacunocbackend.dto.student.StudentRequestAttributeDTO;
import com.hrp.libreriacunocbackend.dto.student.StudentRequestDTO;
import com.hrp.libreriacunocbackend.dto.student.StudentResponseDTO;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.EntityNotFoundException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;

import java.util.List;

public interface StudentService {

    StudentResponseDTO createStudent(StudentRequestDTO studentRequestDTO) throws NotAcceptableException, EntityNotFoundException;

    List<StudentResponseDTO> getByRange(Integer startIndex, Integer endIndex) throws NotAcceptableException, BadRequestException;

    List<StudentResponseDTO> getByAttribute(StudentRequestAttributeDTO studentRequestAttributeDTO) throws NotAcceptableException, BadRequestException;

}
