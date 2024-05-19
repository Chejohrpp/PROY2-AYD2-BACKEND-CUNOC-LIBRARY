package com.hrp.libreriacunocbackend.service.borrow;

import com.hrp.libreriacunocbackend.dto.borrow.*;
import com.hrp.libreriacunocbackend.entities.Borrow;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.EntityNotFoundException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;

import java.util.List;

public interface BorrowService {

    BorrowResponseDTO newBorrow(BorrowRequestDTO borrowRequestDTO) throws NotAcceptableException, EntityNotFoundException;

    BorrowResponseUpdateStateDTO updateState(BorrowRequestUpdateStateDTO borrowRequestUpdateStateDTO) throws EntityNotFoundException;

    BorrowResponseFeeDTO returnBook(BorrowRequestFeeDTO borrowRequestFeeDTO) throws EntityNotFoundException, NotAcceptableException;

    void updateBorrowStatus(List<Borrow> borrows);
}
