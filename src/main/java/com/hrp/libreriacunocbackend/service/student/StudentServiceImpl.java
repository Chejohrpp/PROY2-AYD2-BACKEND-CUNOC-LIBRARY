package com.hrp.libreriacunocbackend.service.student;

import com.hrp.libreriacunocbackend.dto.student.StudentRequestAttributeDTO;
import com.hrp.libreriacunocbackend.dto.student.StudentRequestDTO;
import com.hrp.libreriacunocbackend.dto.student.StudentResponseDTO;
import com.hrp.libreriacunocbackend.dto.user.UserRequestDTO;
import com.hrp.libreriacunocbackend.entities.user.Career;
import com.hrp.libreriacunocbackend.entities.user.Student;
import com.hrp.libreriacunocbackend.entities.user.User;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.EntityNotFoundException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.repository.specifications.user.StudentSpecification;
import com.hrp.libreriacunocbackend.repository.user.StudentRepository;
import com.hrp.libreriacunocbackend.service.career.CareerService;
import com.hrp.libreriacunocbackend.service.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.lang.reflect.Field;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService{

    private final StudentRepository studentRepository;
    private final UserService userService;
    private final CareerService careerService;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository, UserService userService, CareerService careerService) {
        this.studentRepository = studentRepository;
        this.userService = userService;
        this.careerService = careerService;
    }

    @Transactional
    @Override
    public StudentResponseDTO createStudent(StudentRequestDTO studentRequestDTO) throws NotAcceptableException,EntityNotFoundException  {

        /*VALIDATIONS*/
        validateStudentRequest(studentRequestDTO);

        Career career = careerService.findCarrerById(studentRequestDTO.getIdCareer())
                .orElseThrow(() -> new EntityNotFoundException(String.format("career %s not found", studentRequestDTO.getIdCareer())));

        UserRequestDTO userRequestDTO = new UserRequestDTO(studentRequestDTO);
        User newUser = userService.createUser(userRequestDTO);

        Student newStudent = new Student();
        newStudent.setName(studentRequestDTO.getName());
        newStudent.setBirth(LocalDate.from(studentRequestDTO.getBirth()));
        newStudent.setCarnet(studentRequestDTO.getCarnet());
        newStudent.setCanBorrow(true);
        newStudent.setCareer(career);
        newStudent.setUser(newUser);

        newStudent = studentRepository.save(newStudent);

        return new StudentResponseDTO(newStudent);
    }

    @Override
    public List<StudentResponseDTO> getByRange(Integer startIndex, Integer endIndex) throws NotAcceptableException, BadRequestException {
        return studentRepository.findByRange(startIndex,endIndex)
                .stream()
                .map(StudentResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentResponseDTO> getByAttribute(StudentRequestAttributeDTO studentRequestAttributeDTO) throws NotAcceptableException, BadRequestException {
        return studentRepository.findAll(StudentSpecification.likeAttributes(studentRequestAttributeDTO.getAttributesName(), studentRequestAttributeDTO.getFilter()))
                .stream()
                .map(StudentResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public StudentResponseDTO updateStudentStudent(Student student){
        student = studentRepository.save(student);
        return new StudentResponseDTO(student);
    }

    private void validateStudentRequest(StudentRequestDTO studentRequestDTO) throws NotAcceptableException {
        if (studentRequestDTO.getName() == null || studentRequestDTO.getName().isEmpty()) {
            throw new NotAcceptableException("Name cannot be null or empty");
        }
        if (studentRequestDTO.getCarnet() == null || studentRequestDTO.getCarnet().isEmpty()) {
            throw new NotAcceptableException("Carnet cannot be null or empty");
        }
        if (studentRequestDTO.getBirth() == null) {
            throw new NotAcceptableException("Birth cannot be null");
        }
        if (studentRequestDTO.getIdCareer() == null) {
            throw new NotAcceptableException("Career ID cannot be null");
        }
        if (studentRequestDTO.getBirth().isAfter(LocalDate.from(LocalDate.now()).atStartOfDay())) {
            throw new NotAcceptableException("Birth date cannot be in the future");
        }
        if (studentRequestDTO.getName().length() > 50) {
            throw new NotAcceptableException("Name exceeds maximum length");
        }
        if (studentRequestDTO.getCarnet().length() > 20) {
            throw new NotAcceptableException("Carnet exceeds maximum length");
        }
        Optional<Student> foundStudent = studentRepository.findByCarnet(studentRequestDTO.getCarnet());
        if (foundStudent.isPresent()) {
            throw new NotAcceptableException("carnet already taken");
        }
        Optional<User> foundUser = userService.findUser(studentRequestDTO.getUsername());
        if (foundUser.isPresent()){
            throw new NotAcceptableException("Username already taken");
        }
    }

    // Obtain the fields from the student class
    private List<String> getStudentAttributeNames() {
        return Arrays.stream(Student.class.getDeclaredFields())
                .map(Field::getName)
                .map(String::trim)
                .collect(Collectors.toList());
    }

    //
    private boolean isValidAttribute(String attributeName) {
        return getStudentAttributeNames().contains(attributeName);
    }

    @Override
    public Optional<Student> getByCarnet(String carnet){
        return studentRepository.findByCarnet(carnet);
    }

    @Override
    public Optional<Student> getById(Long id){
        return studentRepository.findById(id);
    }

}
