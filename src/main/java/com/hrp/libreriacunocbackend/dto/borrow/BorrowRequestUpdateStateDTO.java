package com.hrp.libreriacunocbackend.dto.borrow;

import lombok.Value;

@Value
public class BorrowRequestUpdateStateDTO {
    Long idBorrow;
    String Status;
}
