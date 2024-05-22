package com.hrp.libreriacunocbackend.dto.report;

import com.hrp.libreriacunocbackend.dto.borrow.BorrowResponseCompleteDTO;
import lombok.Value;

import java.util.List;

@Value
public class StudentMostBorrowIntervalResponseDTO {
    String studentName;
    List<BorrowResponseCompleteDTO> borrows;


}
