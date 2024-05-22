package com.hrp.libreriacunocbackend.dto.borrow;

import com.hrp.libreriacunocbackend.enums.borrow.State;
import lombok.Value;

@Value
public class BorrowResponseFeeDTO {
    Long idBorrow;
    State status;

    public BorrowResponseFeeDTO(BorrowResponseUpdateStateDTO borrowResponseUpdateStateDTO){
        this.idBorrow = borrowResponseUpdateStateDTO.getIdBorrow();
        this.status = borrowResponseUpdateStateDTO.getStatus();
    }
}
