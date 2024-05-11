package com.hrp.libreriacunocbackend.dto.borrow;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class BorrowResponseDTO {
    Long idStudent;
    Long idBook;
    LocalDateTime dateBorrow;
}
