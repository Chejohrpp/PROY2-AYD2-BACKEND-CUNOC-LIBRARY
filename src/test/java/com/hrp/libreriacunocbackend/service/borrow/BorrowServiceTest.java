package com.hrp.libreriacunocbackend.service.borrow;

import com.hrp.libreriacunocbackend.dto.book.BookRequestAmountCopiesDTO;
import com.hrp.libreriacunocbackend.dto.book.BookResponseDTO;
import com.hrp.libreriacunocbackend.dto.borrow.*;
import com.hrp.libreriacunocbackend.dto.fee.FeeRequestDTO;
import com.hrp.libreriacunocbackend.dto.fee.FeeResponseDTO;
import com.hrp.libreriacunocbackend.dto.student.StudentResponseDTO;
import com.hrp.libreriacunocbackend.entities.Borrow;
import com.hrp.libreriacunocbackend.entities.Fee;
import com.hrp.libreriacunocbackend.entities.book.Author;
import com.hrp.libreriacunocbackend.entities.book.Book;
import com.hrp.libreriacunocbackend.entities.book.Editorial;
import com.hrp.libreriacunocbackend.entities.user.Career;
import com.hrp.libreriacunocbackend.entities.user.Student;
import com.hrp.libreriacunocbackend.entities.user.User;
import com.hrp.libreriacunocbackend.enums.borrow.State;
import com.hrp.libreriacunocbackend.exceptions.EntityNotFoundException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.repository.BorrowRepository;
import com.hrp.libreriacunocbackend.service.book.BookService;
import com.hrp.libreriacunocbackend.service.fee.FeeService;
import com.hrp.libreriacunocbackend.service.student.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BorrowServiceTest {

    @Mock
    private BorrowRepository borrowRepository;

    @Mock
    private FeeService feeService;

    @Mock
    private StudentService studentService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BorrowServiceImpl borrowService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void newBorrow_successfulCreation() throws NotAcceptableException, EntityNotFoundException {
        // Setup
        BorrowRequestDTO borrowRequestDTO = new BorrowRequestDTO("12345", "isbn-123", LocalDateTime.now());
        Student student = new Student();
        student.setUserId(1L);
        student.setCanBorrow(true);
        Book book = new Book();
        book.setIdBook(1L);
        book.setAmount(5);
        book.setPrice(10.0);
        Borrow borrow = new Borrow();
        borrow.setStudent(student);
        borrow.setBook(book);
        borrow.setDateBorrow(LocalDate.from(borrowRequestDTO.getDateBorrow()));
        borrow.setState(State.BORROW);

        // Mocks
        when(studentService.getByCarnet("12345")).thenReturn(Optional.of(student));
        when(bookService.getByIsbn("isbn-123")).thenReturn(Optional.of(book));
        when(borrowRepository.countByStudentAndState(student, State.BORROW)).thenReturn(0);
        when(borrowRepository.countByStudentAndState(student, State.LATE)).thenReturn(0);
        when(borrowRepository.countByStudentAndState(student, State.LOST)).thenReturn(0);
        when(borrowRepository.save(any(Borrow.class))).thenReturn(borrow);

        // Action
        BorrowResponseDTO result = borrowService.newBorrow(borrowRequestDTO);

        // Verification
        verify(borrowRepository, times(1)).findAll();
        verify(studentService, times(1)).getByCarnet("12345");
        verify(bookService, times(1)).getByIsbn("isbn-123");
        verify(borrowRepository, times(1)).save(any(Borrow.class));
        assertNotNull(result);
        assertEquals(1L, result.getIdStudent());
        assertEquals(1L, result.getIdBook());
        assertEquals(LocalDate.from(borrowRequestDTO.getDateBorrow()), result.getDateBorrow());
    }

    @Test
    void newBorrow_studentCannotBorrow() {
        // Setup
        BorrowRequestDTO borrowRequestDTO = new BorrowRequestDTO("12345", "isbn-123", LocalDateTime.now());
        Student student = new Student();
        student.setCanBorrow(false);

        // Mocks
        when(studentService.getByCarnet("12345")).thenReturn(Optional.of(student));

        // Action and Verification
        Exception exception = assertThrows(NotAcceptableException.class, () -> {
            borrowService.newBorrow(borrowRequestDTO);
        });

        assertEquals("the student can't do borrows", exception.getMessage());
    }

    @Test
    void newBorrow_bookNotAvailable() throws EntityNotFoundException, NotAcceptableException {
        // Setup
        BorrowRequestDTO borrowRequestDTO = new BorrowRequestDTO("12345", "isbn-123", LocalDateTime.now());
        Student student = new Student();
        student.setCanBorrow(true);
        Book book = new Book();
        book.setAmount(0);

        // Mocks
        when(studentService.getByCarnet("12345")).thenReturn(Optional.of(student));
        when(bookService.getByIsbn("isbn-123")).thenReturn(Optional.of(book));

        // Action and Verification
        Exception exception = assertThrows(NotAcceptableException.class, () -> {
            borrowService.newBorrow(borrowRequestDTO);
        });

        assertEquals("No hay copias disponibles del libro deseado.", exception.getMessage());
    }

    @Test
    void newBorrow_studentHasTooManyBooks() throws NotAcceptableException, EntityNotFoundException {
        // Setup
        BorrowRequestDTO borrowRequestDTO = new BorrowRequestDTO("12345", "isbn-123", LocalDateTime.now());
        Student student = new Student();
        student.setCanBorrow(true);
        Book book = new Book();
        book.setAmount(5);

        // Mocks
        when(studentService.getByCarnet("12345")).thenReturn(Optional.of(student));
        when(bookService.getByIsbn("isbn-123")).thenReturn(Optional.of(book));
        when(borrowRepository.countByStudentAndState(student, State.BORROW)).thenReturn(1);
        when(borrowRepository.countByStudentAndState(student, State.LATE)).thenReturn(1);
        when(borrowRepository.countByStudentAndState(student, State.LOST)).thenReturn(1);

        // Action and Verification
        Exception exception = assertThrows(NotAcceptableException.class, () -> {
            borrowService.newBorrow(borrowRequestDTO);
        });

        assertEquals("El estudiante ya tiene tres libros prestados.", exception.getMessage());
    }

    @Test
    void updateState() {
    }

    @Test
    void findBorrow() {
    }

    @Test
    void returnBook_successfulReturn() throws EntityNotFoundException, NotAcceptableException {
        // Setup
        BorrowRequestFeeDTO borrowRequestFeeDTO = new BorrowRequestFeeDTO(1L, LocalDateTime.now(), 5.0, 2.0, 10.0);
        Borrow borrow = new Borrow();
        borrow.setIdBorrow(1L);
        borrow.setState(State.BORROW);
        Student student = new Student();
        student.setUserId(1L);
        student.setUser(new User());
        student.setCareer(new Career());
        borrow.setStudent(student);
        Book book = new Book();
        book.setIsbn("isbn-123");
        book.setEditorial(new Editorial());
        book.setAuthor(new Author());
        borrow.setBook(book);

        Fee fee = new Fee();
        fee.setBorrow(borrow);
        fee.setBorrowId(1L);
        fee.setFee(10.0);
        fee.setLateFee(7.0);

        // Mocks
        when(borrowRepository.findById(1L)).thenReturn(Optional.of(borrow));
        when(studentService.updateStudentStudent(any(Student.class))).thenReturn( new StudentResponseDTO(student));
        when(bookService.UpdateAmountCopies(any(BookRequestAmountCopiesDTO.class))).thenReturn(new BookResponseDTO(book));
        when(feeService.create(any(FeeRequestDTO.class))).thenReturn(new FeeResponseDTO(fee));
        when(borrowRepository.save(any(Borrow.class))).thenReturn(borrow);

        // Action
        BorrowResponseFeeDTO result = borrowService.returnBook(borrowRequestFeeDTO);

        // Verification
        verify(borrowRepository, times(2)).findById(1L);
        verify(bookService, times(1)).UpdateAmountCopies(any(BookRequestAmountCopiesDTO.class));
        verify(feeService, times(1)).create(any(FeeRequestDTO.class));
        verify(studentService, times(0)).updateStudentStudent(any(Student.class));
        verify(borrowRepository, times(1)).save(borrow);
        assertNotNull(result);
        assertEquals(1L, result.getIdBorrow());
        assertEquals(State.RETURN, result.getStatus());
    }

    @Test
    void getMostBorrowedCareerInInterval() {
    }

    @Test
    void getLateFeesAndLateBorrowsByStudentAndInterval() {
    }

    @Test
    void getStudentWithMostBorrowsInInterval() {
    }

    @Test
    void getActivateBorrowsByStudent() {
    }

    @Test
    void getCurrentBorrowsByStudent() {
    }

    @Test
    void updateBorrowStatus_borrowWithinPeriod_noChange() {
        Borrow borrow = new Borrow();
        borrow.setDateBorrow(LocalDate.now().minusDays(2));
        borrow.setState(State.BORROW);
        borrow.setStudent(new Student());

        borrowService.updateBorrowStatus(List.of(borrow));

        assertEquals(State.BORROW, borrow.getState());
        verify(borrowRepository, never()).save(borrow);
    }

    @Test
    void updateBorrowStatus_borrowLate_changeToLate() {
        Borrow borrow = new Borrow();
        borrow.setDateBorrow(LocalDate.now().minusDays(4));
        borrow.setState(State.BORROW);
        borrow.setStudent(new Student());

        borrowService.updateBorrowStatus(List.of(borrow));

        assertEquals(State.LATE, borrow.getState());
        verify(borrowRepository, times(1)).save(borrow);
    }

    @Test
    void updateBorrowStatus_borrowLost_changeToLostAndStudentCannotBorrow() {
        Student student = new Student();
        Borrow borrow = new Borrow();
        borrow.setDateBorrow(LocalDate.now().minusMonths(2));
        borrow.setState(State.BORROW);
        borrow.setStudent(student);

        borrowService.updateBorrowStatus(List.of(borrow));

        assertEquals(State.LOST, borrow.getState());
        assertFalse(student.getCanBorrow());
        verify(borrowRepository, times(2)).save(borrow);
        verify(studentService, times(1)).updateStudentStudent(student);
    }

    @Test
    void updateBorrowStatus_borrowNull_throwsException() {
        List<Borrow> borrows = Collections.singletonList((Borrow) null);

        assertThrows(IllegalArgumentException.class, () -> {
            borrowService.updateBorrowStatus(borrows);
        });
    }

    @Test
    void calculatePenaltyAmount() {
    }

    @Test
    void getAll_withNoBorrowRecords_returnsEmptyList() {
        when(borrowRepository.findAll()).thenReturn(Collections.emptyList());

        List<BorrowResponseCompleteDTO> result = borrowService.getAll();

        verify(borrowRepository, times(2)).findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void getLateBorrows() {
    }

    @Test
    void getBorrowsToBeReturnedToday() {
    }

    @Test
    void getBorrowsToBeReturnedOnDate() {
    }

    @Test
    void getTotalRevenueInInterval() {
    }
}