package com.hrp.libreriacunocbackend.dto.reservation;

import com.hrp.libreriacunocbackend.entities.Reservation;
import lombok.Value;

import java.time.LocalDate;

@Value
public class ReservationResponseRangeDTO {
    Long id;
    LocalDate date;
    Boolean isActivate;
    String studentName;
    String studentCarnet;
    String bookTitle;

    public ReservationResponseRangeDTO(Reservation reservation){
        this.id = reservation.getIdReservation();
        this.date = reservation.getDate();
        this.isActivate = reservation.getIsActivate();
        this.studentName = reservation.getStudent().getName();
        this.studentCarnet = reservation.getStudent().getCarnet();
        this.bookTitle = reservation.getBook().getTitle();
    }
}
