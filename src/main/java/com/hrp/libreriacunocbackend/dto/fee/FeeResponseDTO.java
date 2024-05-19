package com.hrp.libreriacunocbackend.dto.fee;

import com.hrp.libreriacunocbackend.entities.Fee;
import lombok.Value;

@Value
public class FeeResponseDTO {
    Long idBorrow;
    Double fee;
    Double lateFee;

    public FeeResponseDTO(Fee fee){
        this.fee = fee.getFee();
        this.lateFee = fee.getLateFee();
        this.idBorrow = fee.getBorrow().getIdBorrow();
    }

}
