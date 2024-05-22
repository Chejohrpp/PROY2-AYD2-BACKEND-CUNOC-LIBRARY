package com.hrp.libreriacunocbackend.dto.report;

import com.hrp.libreriacunocbackend.dto.borrow.BorrowResponseCompleteDTO;
import com.hrp.libreriacunocbackend.entities.user.Career;
import lombok.Value;

import java.util.List;

@Value
public class BorrowedCareerIntervalResponseDTO {
    String careerName;
    Integer totalRegisters;
    List<BorrowResponseCompleteDTO> borrows;

    public BorrowedCareerIntervalResponseDTO(Career career, Integer totalRegisters, List<BorrowResponseCompleteDTO> borrows){
        this.careerName = career.getName();
        this.totalRegisters = totalRegisters;
        this.borrows = borrows;

    }

}
