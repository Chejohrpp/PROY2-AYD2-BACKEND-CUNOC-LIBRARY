package com.hrp.libreriacunocbackend.service.fee;

import com.hrp.libreriacunocbackend.dto.fee.FeeRequestDTO;
import com.hrp.libreriacunocbackend.dto.fee.FeeResponseDTO;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;

public interface FeeService {

    FeeResponseDTO create(FeeRequestDTO feeRequestDTO) throws NotAcceptableException, BadRequestException;



}
