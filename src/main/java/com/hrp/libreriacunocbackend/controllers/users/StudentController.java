package com.hrp.libreriacunocbackend.controllers.users;

import com.hrp.libreriacunocbackend.dto.student.StudentRequestAttributeDTO;
import com.hrp.libreriacunocbackend.dto.student.StudentRequestDTO;
import com.hrp.libreriacunocbackend.dto.student.StudentResponseDTO;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.DuplicatedEntityException;
import com.hrp.libreriacunocbackend.exceptions.EntityNotFoundException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.service.student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@PreAuthorize("hasRole('LIBRARIAN')")
@RequestMapping("/v1/student")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("create")
    public ResponseEntity<StudentResponseDTO> createStudent(@RequestBody StudentRequestDTO studentRequestDTO) throws NotAcceptableException, EntityNotFoundException, DuplicatedEntityException {
        return ResponseEntity.status(HttpStatus.OK).body(studentService.createStudent(studentRequestDTO));
    }

    @GetMapping("get_attribute_filter")
    public ResponseEntity<List<StudentResponseDTO>> getByAttributeFilter(@RequestBody StudentRequestAttributeDTO studentRequestAttributeDTO) throws NotAcceptableException, BadRequestException {
        return ResponseEntity.status(HttpStatus.OK).body(studentService.getByAttribute(studentRequestAttributeDTO));
    }

    @GetMapping("get_range/{startIndex}/{endIndex}")
    public ResponseEntity<List<StudentResponseDTO>> getByRange(@PathVariable(name = "startIndex") Integer startIndex, @PathVariable(name = "endIndex") Integer endIndex) throws NotAcceptableException, BadRequestException {
        return ResponseEntity.status(HttpStatus.OK).body(studentService.getByRange(startIndex,endIndex));
    }
}
