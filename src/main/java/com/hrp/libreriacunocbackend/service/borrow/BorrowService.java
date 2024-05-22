package com.hrp.libreriacunocbackend.service.borrow;

import com.hrp.libreriacunocbackend.dto.borrow.*;
import com.hrp.libreriacunocbackend.dto.report.BorrowedCareerIntervalResponseDTO;
import com.hrp.libreriacunocbackend.dto.report.LateFeeBorrowsStudentIntervalResponseDTO;
import com.hrp.libreriacunocbackend.dto.report.StudentMostBorrowIntervalResponseDTO;
import com.hrp.libreriacunocbackend.dto.report.TotalRevenueIntervalResponseDTO;
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

    BorrowResponseFeaturesFeeDTO findBorrow(Long id) throws EntityNotFoundException, NotAcceptableException;

    BorrowResponseFeeDTO returnBook(BorrowRequestFeeDTO borrowRequestFeeDTO) throws EntityNotFoundException, NotAcceptableException;

    BorrowedCareerIntervalResponseDTO getMostBorrowedCareerInInterval(LocalDate startDate, LocalDate endDate);

    LateFeeBorrowsStudentIntervalResponseDTO getLateFeesAndLateBorrowsByStudentAndInterval(Long studentId, LocalDate startDate, LocalDate endDate);

    StudentMostBorrowIntervalResponseDTO getStudentWithMostBorrowsInInterval(LocalDate startDate, LocalDate endDate);

    List<Borrow> getActivateBorrowsByStudent(Long studentId);

    List<BorrowResponseCompleteDTO> getCurrentBorrowsByStudent(Long studentId);

    void updateBorrowStatus(List<Borrow> borrows);

    double calculatePenaltyAmount(Long studentId) throws NotAcceptableException;

    long count();

    List<BorrowResponseCompleteDTO> getAll();

    List<BorrowResponseCompleteDTO> getLateBorrows();

    List<BorrowResponseCompleteDTO> getBorrowsToBeReturnedToday(LocalDate date);

    List<BorrowResponseCompleteDTO> getBorrowsToBeReturnedOnDate(LocalDate date);

    TotalRevenueIntervalResponseDTO getTotalRevenueInInterval(LocalDate startDate, LocalDate endDate);
}
