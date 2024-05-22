package com.hrp.libreriacunocbackend.dto.reservation;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class ReservatonRequestDTO {
    LocalDateTime date;
    String username;
    Long idBook;
}
