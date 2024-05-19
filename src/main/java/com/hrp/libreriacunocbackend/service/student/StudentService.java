package com.hrp.libreriacunocbackend.service.student;

import com.hrp.libreriacunocbackend.dto.student.StudentRequestAttributeDTO;
import com.hrp.libreriacunocbackend.dto.student.StudentRequestDTO;
import com.hrp.libreriacunocbackend.dto.student.StudentResponseDTO;
import com.hrp.libreriacunocbackend.entities.user.Student;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.EntityNotFoundException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    StudentResponseDTO createStudent(StudentRequestDTO studentRequestDTO) throws NotAcceptableException, EntityNotFoundException;

    List<StudentResponseDTO> getByRange(Integer startIndex, Integer endIndex) throws NotAcceptableException, BadRequestException;

    List<StudentResponseDTO> getByAttribute(StudentRequestAttributeDTO studentRequestAttributeDTO) throws NotAcceptableException, BadRequestException;

    StudentResponseDTO updateStudentStudent(Student student);

    Optional<Student> getByCarnet(String carnet);

    Optional<Student> getById(Long id);

    List<Student> getAll();

    List<Student> getStudentsInPenalty();
}
