package com.hrp.libreriacunocbackend.service.fee;

import com.hrp.libreriacunocbackend.dto.fee.FeeRequestDTO;
import com.hrp.libreriacunocbackend.dto.fee.FeeResponseDTO;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.repository.FeeRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class FeeServiceImpl implements FeeService{
    private final FeeRepository feeRepository;

    @Autowired
    public FeeServiceImpl(FeeRepository feeRepository) {
        this.feeRepository = feeRepository;
    }

    @Override
    public FeeResponseDTO create(FeeRequestDTO feeRequestDTO) throws NotAcceptableException {

        return null;
    }
}
