package com.hrp.libreriacunocbackend.service.fee;

import com.hrp.libreriacunocbackend.dto.fee.FeeRequestDTO;
import com.hrp.libreriacunocbackend.dto.fee.FeeResponseDTO;
import com.hrp.libreriacunocbackend.entities.Fee;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;

import java.time.LocalDate;
import java.util.List;

public interface FeeService {

    FeeResponseDTO create(FeeRequestDTO feeRequestDTO) throws NotAcceptableException;


    List<Fee> findLateFeesByStudentAndInterval(Long studentId, LocalDate startDate, LocalDate endDate);
}
