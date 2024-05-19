package com.hrp.libreriacunocbackend.dto.borrow;

import com.hrp.libreriacunocbackend.enums.borrow.State;
import lombok.Value;

@Value
public class BorrowRequestUpdateStateDTO {
    Long idBorrow;
    State Status;
}
