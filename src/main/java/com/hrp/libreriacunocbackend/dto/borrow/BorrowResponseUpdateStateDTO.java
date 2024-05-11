package com.hrp.libreriacunocbackend.dto.borrow;

import lombok.Value;

@Value
public class BorrowResponseUpdateStateDTO {
    Long idBorrow;
    String Status;
}
