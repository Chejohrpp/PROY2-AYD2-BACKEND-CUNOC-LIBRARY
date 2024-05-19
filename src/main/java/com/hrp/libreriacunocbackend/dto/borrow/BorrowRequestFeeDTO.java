package com.hrp.libreriacunocbackend.dto.borrow;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class BorrowRequestFeeDTO {
    Long idBorrow;
    LocalDateTime date;
}
