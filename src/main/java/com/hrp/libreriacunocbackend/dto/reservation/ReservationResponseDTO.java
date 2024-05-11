package com.hrp.libreriacunocbackend.dto.reservation;

import lombok.Value;

@Value
public class ReservationResponseDTO {
    String date;
    Long idUser;
    Long idBook;
    Boolean isActive;
}
