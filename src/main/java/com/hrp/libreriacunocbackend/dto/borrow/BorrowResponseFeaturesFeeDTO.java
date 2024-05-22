package com.hrp.libreriacunocbackend.dto.borrow;

import lombok.Value;

import java.time.LocalDate;

@Value
public class BorrowResponseFeaturesFeeDTO {
    Long idBorrow;
    String studentName;
    String bookTitle;
    LocalDate dateBorrow;
    Double lateFee;
    Double penalty;
    Double regularFee;

}
