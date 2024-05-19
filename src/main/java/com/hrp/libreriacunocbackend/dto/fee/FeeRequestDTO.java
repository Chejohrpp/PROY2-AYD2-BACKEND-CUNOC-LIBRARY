package com.hrp.libreriacunocbackend.dto.fee;

import com.hrp.libreriacunocbackend.entities.Borrow;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class FeeRequestDTO {
    Borrow borrow;
    LocalDateTime date;
    Double fee;
    Double lateFee;
}
