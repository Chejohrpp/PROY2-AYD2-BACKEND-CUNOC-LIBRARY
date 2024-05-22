package com.hrp.libreriacunocbackend.dto.report;

import com.hrp.libreriacunocbackend.dto.borrow.BorrowResponseCompleteDTO;
import com.hrp.libreriacunocbackend.dto.fee.FeeReportStudentResponseDTO;
import lombok.Value;

import java.util.List;

@Value
public class LateFeeBorrowsStudentIntervalResponseDTO {
    List<FeeReportStudentResponseDTO> fees;
    List<BorrowResponseCompleteDTO> borrows;
}
