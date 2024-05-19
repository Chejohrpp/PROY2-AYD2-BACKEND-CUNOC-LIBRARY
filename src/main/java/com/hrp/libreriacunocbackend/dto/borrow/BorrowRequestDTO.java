package com.hrp.libreriacunocbackend.dto.borrow;

import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
public class BorrowRequestDTO {
    String carnet;
    String isbn;
    LocalDateTime dateBorrow;
}
