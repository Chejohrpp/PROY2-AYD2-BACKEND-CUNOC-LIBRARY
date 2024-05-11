package com.hrp.libreriacunocbackend.dto.borrow;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class BorrowRequestDTO {
    String carnet;
    Long idBook;
    LocalDateTime dateBorrow;
}
