package com.hrp.libreriacunocbackend.dto.borrow;

import com.hrp.libreriacunocbackend.entities.Borrow;
import com.hrp.libreriacunocbackend.enums.borrow.State;
import lombok.Value;

@Value
public class BorrowResponseUpdateStateDTO {
    Long idBorrow;
    State status;

    public BorrowResponseUpdateStateDTO(Borrow borrow){
        this.idBorrow = borrow.getIdBorrow();
        this.status = borrow.getState();
    }
}
