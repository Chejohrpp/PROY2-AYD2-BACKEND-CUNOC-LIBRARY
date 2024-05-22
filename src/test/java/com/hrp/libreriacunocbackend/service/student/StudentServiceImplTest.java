package com.hrp.libreriacunocbackend.service.student;

import com.hrp.libreriacunocbackend.dto.student.StudentRequestAttributeDTO;
import com.hrp.libreriacunocbackend.dto.student.StudentRequestDTO;
import com.hrp.libreriacunocbackend.dto.student.StudentResponseDTO;
import com.hrp.libreriacunocbackend.dto.user.UserRequestDTO;
import com.hrp.libreriacunocbackend.entities.user.Career;
import com.hrp.libreriacunocbackend.entities.user.Student;
import com.hrp.libreriacunocbackend.entities.user.User;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.DuplicatedEntityException;
import com.hrp.libreriacunocbackend.exceptions.EntityNotFoundException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.repository.user.StudentRepository;
import com.hrp.libreriacunocbackend.service.career.CareerService;
import com.hrp.libreriacunocbackend.service.user.UserService;
import com.hrp.libreriacunocbackend.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserService userService;

    @Mock
    private CareerService careerService;

    @InjectMocks
    private StudentServiceImpl studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createStudent_validRequest_createsStudent() throws NotAcceptableException, EntityNotFoundException, DuplicatedEntityException {
        StudentRequestDTO studentRequestDTO = new StudentRequestDTO("john_doe", "John Doe", "12345", LocalDate.of(2000, 1, 1).atStartOfDay(), 1L);

        Career career = new Career();
        career.setIdCareer(1L);
        career.setName("Computer Science");

        User user = new User();
        user.setIdUser(1L);
        user.setUsername("john_doe");

        Student student = new Student();
        student.setCareer(career);
        student.setUser(user);

        when(careerService.findCarrerById(1L)).thenReturn(Optional.of(career));
        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(user);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentResponseDTO responseDTO = studentService.createStudent(studentRequestDTO);

        assertNotNull(responseDTO);
        assertEquals("john_doe", responseDTO.getUsername());
        verify(careerService, times(1)).findCarrerById(1L);
        verify(userService, times(1)).createUser(any(UserRequestDTO.class));
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void createStudent_nullName_throwsNotAcceptableException() {
        StudentRequestDTO studentRequestDTO = new StudentRequestDTO("john_doe", null, "12345", LocalDate.of(2000, 1, 1).atStartOfDay(), 1L);

        NotAcceptableException exception = assertThrows(NotAcceptableException.class, () -> {
            studentService.createStudent(studentRequestDTO);
        });

        assertEquals("Name cannot be null or empty", exception.getMessage());
    }

    @Test
    void createStudent_existingCarnet_throwsDuplicatedEntityException() {
        StudentRequestDTO studentRequestDTO = new StudentRequestDTO("john_doe", "John Doe", "12345", LocalDate.of(2000, 1, 1).atStartOfDay(), 1L);

        when(studentRepository.findByCarnet("12345")).thenReturn(Optional.of(new Student()));

        DuplicatedEntityException exception = assertThrows(DuplicatedEntityException.class, () -> {
            studentService.createStudent(studentRequestDTO);
        });

        assertEquals("carnet already taken", exception.getMessage());
    }

    @Test
    void createStudent_existingUsername_throwsDuplicatedEntityException() {
        StudentRequestDTO studentRequestDTO = new StudentRequestDTO("john_doe", "John Doe", "12345", LocalDate.of(2000, 1, 1).atStartOfDay(), 1L);

        when(userService.findUser("john_doe")).thenReturn(Optional.of(new User()));

        DuplicatedEntityException exception = assertThrows(DuplicatedEntityException.class, () -> {
            studentService.createStudent(studentRequestDTO);
        });

        assertEquals("Username already taken", exception.getMessage());
    }

    @Test
    void createStudent_futureBirthDate_throwsNotAcceptableException() {
        StudentRequestDTO studentRequestDTO = new StudentRequestDTO("john_doe", "John Doe", "12345", LocalDate.of(2100, 1, 1).atStartOfDay(), 1L);

        NotAcceptableException exception = assertThrows(NotAcceptableException.class, () -> {
            studentService.createStudent(studentRequestDTO);
        });

        assertEquals("Birth date cannot be in the future", exception.getMessage());
    }

    @Test
    void createStudent_careerNotFound_throwsEntityNotFoundException() {
        StudentRequestDTO studentRequestDTO = new StudentRequestDTO("john_doe", "John Doe", "12345", LocalDate.of(2000, 1, 1).atStartOfDay(), 1L);

        when(careerService.findCarrerById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            studentService.createStudent(studentRequestDTO);
        });

        assertEquals("career 1 not found", exception.getMessage());
    }

    @Test
    void getByRange_validRange_returnsStudentList() throws NotAcceptableException, BadRequestException {
        User user1 = new User();
        Career career1 = new Career();
        Student student1 = new Student();
        student1.setUser(user1);
        student1.setCareer(career1);
        User user2 = new User();
        Career career2 = new Career();
        Student student2 = new Student();
        student2.setUser(user2);
        student2.setCareer(career2);
        List<Student> students = Arrays.asList(student1, student2);

        when(studentRepository.findByRange(0, 2)).thenReturn(students);

        List<StudentResponseDTO> result = studentService.getByRange(0, 2);

        assertEquals(2, result.size());
        verify(studentRepository, times(1)).findByRange(0, 2);
    }

    @Test
    void getByRange_startIndexNull_throwsBadRequestException() {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            studentService.getByRange(null, 2);
        });

        assertEquals("Los índices de rango no pueden ser nulos.", exception.getMessage());
    }

    @Test
    void getByRange_endIndexNull_throwsBadRequestException() {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            studentService.getByRange(0, null);
        });

        assertEquals("Los índices de rango no pueden ser nulos.", exception.getMessage());
    }

    @Test
    void getByRange_negativeStartIndex_throwsBadRequestException() {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            studentService.getByRange(-1, 2);
        });

        assertEquals("Los índices de rango no pueden ser negativos.", exception.getMessage());
    }

    @Test
    void getByRange_startIndexGreaterThanOrEqualToEndIndex_throwsNotAcceptableException() {
        NotAcceptableException exception = assertThrows(NotAcceptableException.class, () -> {
            studentService.getByRange(2, 2);
        });

        assertEquals("El índice de inicio debe ser menor que el índice de fin.", exception.getMessage());
    }

    @Test
    void getByAttribute_validRequest_returnsStudents() throws NotAcceptableException, BadRequestException {
        StudentRequestAttributeDTO request = new StudentRequestAttributeDTO(List.of("name"), "John");

        Student student = new Student();
        User user = new User();
        user.setIdUser(1L);
        user.setUsername("john_doe");
        student.setUser(user);
        student.setName("John Doe");
        student.setCarnet("12345");
        student.setBirth(LocalDate.of(2000, 1, 1));
        student.setCanBorrow(true);
        Career career = new Career();
        career.setName("Computer Science");
        student.setCareer(career);

        when(studentRepository.findAll(any(Specification.class))).thenReturn(List.of(student));

        List<StudentResponseDTO> response = studentService.getByAttribute(request);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("john_doe", response.get(0).getUsername());
        verify(studentRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void getByAttribute_nullAttributesName_throwsBadRequestException() {
        StudentRequestAttributeDTO request = new StudentRequestAttributeDTO(null, "John");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            studentService.getByAttribute(request);
        });

        assertEquals("AttributesName cannot be null or empty", exception.getMessage());
    }

    @Test
    void getByAttribute_emptyAttributesName_throwsBadRequestException() {
        StudentRequestAttributeDTO request = new StudentRequestAttributeDTO(Collections.emptyList(), "John");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            studentService.getByAttribute(request);
        });

        assertEquals("AttributesName cannot be null or empty", exception.getMessage());
    }

    @Test
    void getByAttribute_nullFilter_throwsBadRequestException() {
        StudentRequestAttributeDTO request = new StudentRequestAttributeDTO(List.of("name"), null);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            studentService.getByAttribute(request);
        });

        assertEquals("Filter cannot be null or empty", exception.getMessage());
    }

    @Test
    void getByAttribute_emptyFilter_throwsBadRequestException() {
        StudentRequestAttributeDTO request = new StudentRequestAttributeDTO(List.of("name"), "");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            studentService.getByAttribute(request);
        });

        assertEquals("Filter cannot be null or empty", exception.getMessage());
    }

    @Test
    void updateStudentStudent_validStudent_returnsUpdatedStudentResponseDTO() {
        // Arrange
        Student student = new Student();
        User user = new User();
        user.setIdUser(1L);
        user.setUsername("john_doe");
        student.setUser(user);
        student.setName("John Doe");
        student.setCarnet("12345");
        student.setBirth(LocalDate.of(2000, 1, 1));
        student.setCanBorrow(true);
        Career career = new Career();
        career.setName("Computer Science");
        student.setCareer(career);

        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // Act
        StudentResponseDTO response = studentService.updateStudentStudent(student);

        // Assert
        assertNotNull(response);
        assertEquals("john_doe", response.getUsername());
        assertEquals("John Doe", response.getName());
        assertEquals("12345", response.getCarnet());
        assertEquals(LocalDate.of(2000, 1, 1), response.getBirth());
        assertEquals("Computer Science", response.getCareerName());
        assertTrue(response.getCanBorrow());
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    void updateStudentStudent_nullStudent_throwsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            studentService.updateStudentStudent(null);
        });
    }

    @Test
    void updateStudentStudent_partialStudent_returnsUpdatedStudentResponseDTO() {
        // Arrange
        Student student = new Student();
        User user = new User();
        user.setIdUser(1L);
        Career career = new Career();
        user.setUsername("john_doe");
        student.setUser(user);
        student.setCareer(career);
        student.setName("John Doe");

        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // Act
        StudentResponseDTO response = studentService.updateStudentStudent(student);

        // Assert
        assertNotNull(response);
        assertEquals("john_doe", response.getUsername());
        assertEquals("John Doe", response.getName());
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    void getByUsername_validUsername_returnsStudent() throws EntityNotFoundException {
        // Arrange
        String username = "john_doe";
        User user = new User();
        user.setIdUser(1L);
        user.setUsername(username);

        Student student = new Student();
        student.setUser(user);

        when(userService.findUser(username)).thenReturn(Optional.of(user));
        when(studentRepository.findById(user.getIdUser())).thenReturn(Optional.of(student));

        // Act
        Optional<Student> result = studentService.getByUsername(username);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user.getIdUser(), result.get().getUser().getIdUser());
        assertEquals(username, result.get().getUser().getUsername());
        verify(userService, times(1)).findUser(username);
        verify(studentRepository, times(1)).findById(user.getIdUser());
    }

    @Test
    void getByUsername_userNotFound_throwsEntityNotFoundException() {
        // Arrange
        String username = "john_doe";

        when(userService.findUser(username)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            studentService.getByUsername(username);
        });

        assertEquals("username john_doe not found", exception.getMessage());
        verify(userService, times(1)).findUser(username);
        verify(studentRepository, times(0)).findById(anyLong());
    }

    @Test
    void getByCarnet_studentExists_returnsStudent() {
        // Arrange
        String carnet = "AB12345";
        Student student = new Student();
        student.setCarnet(carnet);

        when(studentRepository.findByCarnet(carnet)).thenReturn(Optional.of(student));

        // Act
        Optional<Student> result = studentService.getByCarnet(carnet);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(carnet, result.get().getCarnet());
        verify(studentRepository, times(1)).findByCarnet(carnet);
    }

    @Test
    void getByCarnet_studentNotFound_returnsEmptyOptional() {
        // Arrange
        String carnet = "XYZ987";
        when(studentRepository.findByCarnet(carnet)).thenReturn(Optional.empty());

        // Act
        Optional<Student> result = studentService.getByCarnet(carnet);

        // Assert
        assertFalse(result.isPresent());
        verify(studentRepository, times(1)).findByCarnet(carnet);
    }

    @Test
    void getById_studentExists_returnsStudent() {
        // Arrange
        long studentId = 1L;
        Student student = new Student();
        student.setUserId(studentId);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        // Act
        Optional<Student> result = studentService.getById(studentId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(studentId, result.get().getUserId());
        verify(studentRepository, times(1)).findById(studentId);
    }

    @Test
    void getById_studentNotFound_returnsEmptyOptional() {
        // Arrange
        long studentId = 99L;
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // Act
        Optional<Student> result = studentService.getById(studentId);

        // Assert
        assertFalse(result.isPresent());
        verify(studentRepository, times(1)).findById(studentId);
    }

    @Test
    void getAll_studentsExist_returnsListOfStudents() {
        // Arrange
        Student student1 = new Student();
        student1.setUserId(1L);
        Student student2 = new Student();
        student2.setUserId(2L);

        List<Student> expectedStudents = Arrays.asList(student1, student2);

        when(studentRepository.findAll()).thenReturn(expectedStudents);

        // Act
        List<Student> result = studentService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(expectedStudents.size(), result.size());
        assertTrue(result.contains(student1));
        assertTrue(result.contains(student2));
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void getStudentsInPenalty_studentsExist_returnsListOfStudentsInPenalty() {
        // Arrange
        User user1 = new User();
        user1.setIdUser(1L);
        Career career1 = new Career();
        Student student1 = new Student();
        student1.setUser(user1);
        student1.setCareer(career1);
        User user2 = new User();
        user2.setIdUser(2L);
        Career career2 = new Career();
        Student student2 = new Student();
        student2.setUser(user2);
        student2.setCareer(career2);

        List<Student> studentsInPenalty = Arrays.asList(student1, student2);

        when(studentRepository.findStudentsInPenalty()).thenReturn(studentsInPenalty);

        // Act
        List<StudentResponseDTO> result = studentService.getStudentsInPenalty();

        // Assert
        assertNotNull(result);
        assertEquals(studentsInPenalty.size(), result.size());
        for (Student student : studentsInPenalty) {
            assertTrue(result.stream().anyMatch(dto -> dto.getId().equals(student.getUser().getIdUser())));
        }
        verify(studentRepository, times(1)).findStudentsInPenalty();
    }
}