package com.hrp.libreriacunocbackend.dto.reservation;

import lombok.Value;

@Value
public class ReservatonRequestDTO {
    String date;
    Long idUser;
    Long idBook;
}
