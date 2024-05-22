package com.hrp.libreriacunocbackend.service.fee;

import com.hrp.libreriacunocbackend.dto.fee.FeeRequestDTO;
import com.hrp.libreriacunocbackend.dto.fee.FeeResponseDTO;
import com.hrp.libreriacunocbackend.entities.Borrow;
import com.hrp.libreriacunocbackend.entities.Fee;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.repository.FeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeeServiceImplTest {

    @Mock
    private FeeRepository feeRepository;

    @InjectMocks
    private FeeServiceImpl feeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createFee_success() throws NotAcceptableException {
        Borrow borrow = new Borrow();
        borrow.setIdBorrow(1L);
        LocalDateTime date = LocalDateTime.now();
        FeeRequestDTO feeRequestDTO = new FeeRequestDTO(borrow, date, 100.0, 15.0);

        Fee fee = new Fee();
        fee.setBorrow(borrow);
        fee.setDate(date.toLocalDate());
        fee.setFee(100.0);
        fee.setLateFee(15.0);

        when(feeRepository.save(any(Fee.class))).thenReturn(fee);

        FeeResponseDTO feeResponseDTO = feeService.create(feeRequestDTO);

        assertNotNull(feeResponseDTO);
        assertEquals(100.0, feeResponseDTO.getFee());
        assertEquals(15.0, feeResponseDTO.getLateFee());
        assertEquals(1L, feeResponseDTO.getIdBorrow());
        verify(feeRepository, times(1)).save(any(Fee.class));
    }

    @Test
    void createFee_borrowIsNull() {
        LocalDateTime date = LocalDateTime.now();
        FeeRequestDTO feeRequestDTO = new FeeRequestDTO(null, date, 100.0, 15.0);

        NotAcceptableException exception = assertThrows(NotAcceptableException.class, () -> {
            feeService.create(feeRequestDTO);
        });

        assertEquals("Borrow cannot be null", exception.getMessage());
    }

    @Test
    void createFee_dateIsNull() {
        Borrow borrow = new Borrow();
        borrow.setIdBorrow(1L);
        FeeRequestDTO feeRequestDTO = new FeeRequestDTO(borrow, null, 100.0, 15.0);

        NotAcceptableException exception = assertThrows(NotAcceptableException.class, () -> {
            feeService.create(feeRequestDTO);
        });

        assertEquals("Date cannot be null", exception.getMessage());
    }


    @Test
    void createFee_feeIsNull() {
        Borrow borrow = new Borrow();
        borrow.setIdBorrow(1L);
        LocalDateTime date = LocalDateTime.now();
        FeeRequestDTO feeRequestDTO = new FeeRequestDTO(borrow, date, null, 15.0);

        NotAcceptableException exception = assertThrows(NotAcceptableException.class, () -> {
            feeService.create(feeRequestDTO);
        });

        assertEquals("Fee cannot be null", exception.getMessage());
    }

    @Test
    void createFee_lateFeeIsNull() {
        Borrow borrow = new Borrow();
        borrow.setIdBorrow(1L);
        LocalDateTime date = LocalDateTime.now();
        FeeRequestDTO feeRequestDTO = new FeeRequestDTO(borrow, date, 100.0, null);

        NotAcceptableException exception = assertThrows(NotAcceptableException.class, () -> {
            feeService.create(feeRequestDTO);
        });

        assertEquals("Late fee cannot be null", exception.getMessage());
    }

    @Test
    void findLateFeesByStudentAndInterval_feesFound() {
        Long studentId = 1L;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        Fee fee = new Fee();
        fee.setBorrowId(1L);
        fee.setDate(LocalDate.of(2023, 6, 15));
        fee.setFee(50.0);
        fee.setLateFee(10.0);

        List<Fee> expectedFees = List.of(fee);

        when(feeRepository.findLateFeesByStudentAndInterval(studentId, startDate, endDate)).thenReturn(expectedFees);

        List<Fee> actualFees = feeService.findLateFeesByStudentAndInterval(studentId, startDate, endDate);

        assertNotNull(actualFees);
        assertFalse(actualFees.isEmpty());
        assertEquals(expectedFees.size(), actualFees.size());
        assertEquals(expectedFees.get(0).getBorrowId(), actualFees.get(0).getBorrowId());
        verify(feeRepository, times(1)).findLateFeesByStudentAndInterval(studentId, startDate, endDate);
    }

    @Test
    void findLateFeesByStudentAndInterval_noFeesFound() {
        Long studentId = 1L;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        when(feeRepository.findLateFeesByStudentAndInterval(studentId, startDate, endDate)).thenReturn(Collections.emptyList());

        List<Fee> actualFees = feeService.findLateFeesByStudentAndInterval(studentId, startDate, endDate);

        assertNotNull(actualFees);
        assertTrue(actualFees.isEmpty());
        verify(feeRepository, times(1)).findLateFeesByStudentAndInterval(studentId, startDate, endDate);
    }
}