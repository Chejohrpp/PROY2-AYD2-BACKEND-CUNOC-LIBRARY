package com.hrp.libreriacunocbackend.dto.fee;

import com.hrp.libreriacunocbackend.entities.Fee;
import lombok.Value;

import java.time.LocalDate;

@Value
public class FeeReportStudentResponseDTO {
    Double lateFee;
    LocalDate date;
    String bookTitle;

    public FeeReportStudentResponseDTO(Fee fee){
        this.lateFee = fee.getLateFee();
        this.date = fee.getDate();
        this.bookTitle = fee.getBorrow().getBook().getTitle();
    }
}
