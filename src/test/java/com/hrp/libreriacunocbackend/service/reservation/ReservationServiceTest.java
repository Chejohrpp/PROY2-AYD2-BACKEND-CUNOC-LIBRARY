package com.hrp.libreriacunocbackend.service.reservation;

import com.hrp.libreriacunocbackend.dto.reservation.ReservationResponseDTO;
import com.hrp.libreriacunocbackend.dto.reservation.ReservationResponseRangeDTO;
import com.hrp.libreriacunocbackend.dto.reservation.ReservatonRequestDTO;
import com.hrp.libreriacunocbackend.entities.Reservation;
import com.hrp.libreriacunocbackend.entities.book.Book;
import com.hrp.libreriacunocbackend.entities.user.Student;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.EntityNotFoundException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.repository.ReservationRepository;
import com.hrp.libreriacunocbackend.service.book.BookService;
import com.hrp.libreriacunocbackend.service.student.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private StudentService studentService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createReservation_success() throws NotAcceptableException, EntityNotFoundException {
        // Datos de ejemplo
        Student student = new Student();
        student.setUserId(1L);
        student.setName("John Doe");
        student.setCarnet("12345");

        Book book = new Book();
        book.setIdBook(1L);
        book.setTitle("Effective Java");
        book.setAmount(0);

        ReservatonRequestDTO reservationRequestDTO = new ReservatonRequestDTO(LocalDateTime.now(),"John Doe", 1L);

        when(studentService.getByUsername(anyString())).thenReturn(Optional.of(student));
        when(bookService.getById(anyLong())).thenReturn(Optional.of(book));

        Reservation reservation = new Reservation();
        reservation.setIdReservation(1L);
        reservation.setDate(LocalDate.now());
        reservation.setIsActivate(true);
        reservation.setStudent(student);
        reservation.setBook(book);

        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // Llamada al método
        ReservationResponseDTO result = reservationService.create(reservationRequestDTO);

        // Verificaciones
        assertNotNull(result);
        assertEquals(LocalDate.now().toString(), result.getDate());
        assertEquals(1L, result.getIdUser());
        assertEquals(1L, result.getIdBook());
        assertTrue(result.getIsActive());

        verify(studentService, times(1)).getByUsername("John Doe");
        verify(bookService, times(1)).getById(1L);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void createReservation_studentNotFound_throwsEntityNotFoundException() throws EntityNotFoundException {
        // Datos de ejemplo
        ReservatonRequestDTO reservationRequestDTO = new ReservatonRequestDTO(LocalDateTime.now(),"John Doe", 1L);

        when(studentService.getByUsername(anyString())).thenReturn(Optional.empty());

        // Llamada al método y verificación de la excepción
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            reservationService.create(reservationRequestDTO);
        });

        assertEquals("Estudiante no encontrado con username: John Doe", thrown.getMessage());

        verify(studentService, times(1)).getByUsername("John Doe");
        verify(bookService, times(0)).getById(anyLong());
        verify(reservationRepository, times(0)).save(any(Reservation.class));
    }

    @Test
    void createReservation_bookNotFound_throwsNotAcceptableException() throws EntityNotFoundException {
        // Datos de ejemplo
        Student student = new Student();
        student.setUserId(1L);
        student.setName("John Doe");
        student.setCarnet("12345");

        ReservatonRequestDTO reservationRequestDTO = new ReservatonRequestDTO(LocalDateTime.now(),"John Doe", 1L);

        when(studentService.getByUsername(anyString())).thenReturn(Optional.of(student));
        when(bookService.getById(anyLong())).thenReturn(Optional.empty());

        // Llamada al método y verificación de la excepción
        NotAcceptableException thrown = assertThrows(NotAcceptableException.class, () -> {
            reservationService.create(reservationRequestDTO);
        });

        assertEquals("Libro no encontrado con ID: 1", thrown.getMessage());

        verify(studentService, times(1)).getByUsername("John Doe");
        verify(bookService, times(1)).getById(1L);
        verify(reservationRepository, times(0)).save(any(Reservation.class));
    }

    @Test
    void createReservation_bookAvailable_throwsNotAcceptableException() throws EntityNotFoundException {
        // Datos de ejemplo
        Student student = new Student();
        student.setUserId(1L);
        student.setName("John Doe");
        student.setCarnet("12345");

        Book book = new Book();
        book.setIdBook(1L);
        book.setTitle("Effective Java");
        book.setAmount(1);

        ReservatonRequestDTO reservationRequestDTO = new ReservatonRequestDTO(LocalDateTime.now(),"John Doe", 1L);

        when(studentService.getByUsername(anyString())).thenReturn(Optional.of(student));
        when(bookService.getById(anyLong())).thenReturn(Optional.of(book));

        // Llamada al método y verificación de la excepción
        NotAcceptableException thrown = assertThrows(NotAcceptableException.class, () -> {
            reservationService.create(reservationRequestDTO);
        });

        assertEquals("El libro ya está disponible para préstamo, no se puede reservar.", thrown.getMessage());

        verify(studentService, times(1)).getByUsername("John Doe");
        verify(bookService, times(1)).getById(1L);
        verify(reservationRepository, times(0)).save(any(Reservation.class));
    }

    @Test
    void getByRange_success() throws NotAcceptableException, BadRequestException {
        // Datos de ejemplo
        Student student = new Student();
        student.setName("John Doe");
        student.setCarnet("12345");

        Book book = new Book();
        book.setTitle("Effective Java");

        Reservation reservation = new Reservation();
        reservation.setIdReservation(1L);
        reservation.setDate(LocalDate.now());
        reservation.setIsActivate(true);
        reservation.setStudent(student);
        reservation.setBook(book);

        List<Reservation> reservations = List.of(reservation);

        // Configuración del mock
        when(reservationRepository.findByRange(anyInt(), anyInt())).thenReturn(reservations);

        // Llamada al método
        List<ReservationResponseRangeDTO> result = reservationService.getByRange(0, 10);

        // Verificaciones
        assertNotNull(result);
        assertEquals(1, result.size());

        ReservationResponseRangeDTO dto = result.get(0);
        assertEquals(1L, dto.getId());
        assertEquals("John Doe", dto.getStudentName());
        assertEquals("12345", dto.getStudentCarnet());
        assertEquals("Effective Java", dto.getBookTitle());
        assertTrue(dto.getIsActivate());

        verify(reservationRepository, times(1)).findByRange(0, 10);
    }

    @Test
    void getByRange_invalidRange_throwsNotAcceptableException() {
        // Indices inválidos
        int startIndex = -1;
        int endIndex = -5;

        // Llamada al método y verificación de la excepción
        assertThrows(NotAcceptableException.class, () -> {
            reservationService.getByRange(startIndex, endIndex);
        });

        verify(reservationRepository, times(0)).findByRange(anyInt(), anyInt());
    }

    @Test
    void getByRange_startIndexGreaterThanEndIndex_throwsBadRequestException() {
        // Indices inválidos
        int startIndex = 10;
        int endIndex = 5;

        // Llamada al método y verificación de la excepción
        assertThrows(BadRequestException.class, () -> {
            reservationService.getByRange(startIndex, endIndex);
        });

        verify(reservationRepository, times(0)).findByRange(anyInt(), anyInt());
    }

    @Test
    void getByRange_nullIndices_throwsNotAcceptableException() {
        // Índices nulos
        Integer startIndex = null;
        Integer endIndex = null;

        // Llamada al método y verificación de la excepción
        assertThrows(NotAcceptableException.class, () -> {
            reservationService.getByRange(startIndex, endIndex);
        });

        verify(reservationRepository, times(0)).findByRange(anyInt(), anyInt());
    }

}