package com.hrp.libreriacunocbackend.service.borrow;

import com.hrp.libreriacunocbackend.dto.borrow.BorrowRequestDTO;
import com.hrp.libreriacunocbackend.dto.borrow.BorrowRequestUpdateStateDTO;
import com.hrp.libreriacunocbackend.dto.borrow.BorrowResponseDTO;
import com.hrp.libreriacunocbackend.dto.borrow.BorrowResponseUpdateStateDTO;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.EntityNotFoundException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;

public interface BorrowService {

    BorrowResponseDTO newBorrow(BorrowRequestDTO borrowRequestDTO) throws NotAcceptableException;

    BorrowResponseUpdateStateDTO updateState(BorrowRequestUpdateStateDTO borrowRequestUpdateStateDTO) throws EntityNotFoundException, BadRequestException;




}
