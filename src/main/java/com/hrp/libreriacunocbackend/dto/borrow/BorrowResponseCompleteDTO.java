package com.hrp.libreriacunocbackend.dto.borrow;

import com.hrp.libreriacunocbackend.entities.Borrow;
import com.hrp.libreriacunocbackend.enums.borrow.State;
import lombok.Value;

import java.time.LocalDate;

@Value
public class BorrowResponseCompleteDTO {
    Long idBorrow;
    LocalDate dateBorrow;
    State state;
    Double priceBorrow;
    String studentName;
    String studentCarnet;
    String bookTitle;

    public BorrowResponseCompleteDTO(Borrow borrow){
        this.idBorrow = borrow.getIdBorrow();
        this.dateBorrow = borrow.getDateBorrow();
        this.state = borrow.getState();
        this.priceBorrow = borrow.getPriceBorrow();
        this.studentName = borrow.getStudent().getName();
        this.studentCarnet = borrow.getStudent().getCarnet();
        this.bookTitle = borrow.getBook().getTitle();
    }
}
