package com.hrp.libreriacunocbackend.dto.borrow;

import com.hrp.libreriacunocbackend.entities.Borrow;
import lombok.Value;

import java.time.LocalDate;

@Value
public class BorrowResponseDTO {
    Long idStudent;
    Long idBook;
    LocalDate dateBorrow;

    public BorrowResponseDTO(Borrow borrow){
        this.idStudent = borrow.getStudent().getUserId();
        this.idBook = borrow.getBook().getIdBook();
        this.dateBorrow = borrow.getDateBorrow();
    }
}
