package com.hrp.libreriacunocbackend.service.reservation;

import com.hrp.libreriacunocbackend.dto.reservation.ReservationResponseDTO;
import com.hrp.libreriacunocbackend.dto.reservation.ReservationResponseRangeDTO;
import com.hrp.libreriacunocbackend.dto.reservation.ReservatonRequestDTO;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;

import java.util.List;

public interface ReservationService {

    ReservationResponseDTO create(ReservatonRequestDTO reservatonRequestDTO) throws NotAcceptableException;

    List<ReservationResponseRangeDTO> getByRange(Integer startIndex, Integer endIndex) throws NotAcceptableException, BadRequestException;


}
