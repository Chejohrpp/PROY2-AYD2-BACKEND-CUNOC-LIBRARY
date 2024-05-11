package com.hrp.libreriacunocbackend.dto.fee;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class FeeRequestDTO {
    Long idBorrow;
    LocalDateTime date;
    Double fee;
    Double lateFee;
}
