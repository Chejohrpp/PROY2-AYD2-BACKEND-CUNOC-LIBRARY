package com.hrp.libreriacunocbackend.service.fee;

import com.hrp.libreriacunocbackend.dto.fee.FeeRequestDTO;
import com.hrp.libreriacunocbackend.dto.fee.FeeResponseDTO;
import com.hrp.libreriacunocbackend.entities.Fee;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.repository.FeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FeeServiceImpl implements FeeService{
    private final FeeRepository feeRepository;

    @Autowired
    public FeeServiceImpl(FeeRepository feeRepository) {
        this.feeRepository = feeRepository;
    }

    @Override
    public FeeResponseDTO create(FeeRequestDTO feeRequestDTO) throws NotAcceptableException {
        Fee fee = new Fee();
        fee.setFee(feeRequestDTO.getFee());
        fee.setLateFee(fee.getLateFee());
        fee.setBorrow(feeRequestDTO.getBorrow());
        fee.setDate(LocalDate.from(feeRequestDTO.getDate()));
        fee = feeRepository.save(fee);
        return new FeeResponseDTO(fee);
    }

    @Override
    public List<Fee> findLateFeesByStudentAndInterval(Long studentId, LocalDate startDate, LocalDate endDate){
        return feeRepository.findLateFeesByStudentAndInterval(studentId, startDate, endDate);
    }
}
