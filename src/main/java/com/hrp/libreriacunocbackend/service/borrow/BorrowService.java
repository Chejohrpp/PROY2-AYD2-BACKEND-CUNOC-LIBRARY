package com.hrp.libreriacunocbackend.service.borrow;

import com.hrp.libreriacunocbackend.dto.borrow.*;
import com.hrp.libreriacunocbackend.entities.Borrow;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.EntityNotFoundException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BorrowService {

    BorrowResponseDTO newBorrow(BorrowRequestDTO borrowRequestDTO) throws NotAcceptableException, EntityNotFoundException;

    BorrowResponseUpdateStateDTO updateState(BorrowRequestUpdateStateDTO borrowRequestUpdateStateDTO) throws EntityNotFoundException;

    BorrowResponseFeeDTO returnBook(BorrowRequestFeeDTO borrowRequestFeeDTO) throws EntityNotFoundException, NotAcceptableException;

    Map<String, Object> getMostBorrowedCareerInInterval(LocalDate startDate, LocalDate endDate);

    Map<String, Object> getLateFeesAndLateBorrowsByStudentAndInterval(Long studentId, LocalDate startDate, LocalDate endDate);

    Map<String, Object> getStudentWithMostBorrowsInInterval(LocalDate startDate, LocalDate endDate);

    List<Borrow> getActivateBorrowsByStudent(Long studentId);

    List<Borrow> getCurrentBorrowsByStudent(Long studentId);

    void updateBorrowStatus(List<Borrow> borrows);

    double calculatePenaltyAmount(Long studentId) throws NotAcceptableException;
}
