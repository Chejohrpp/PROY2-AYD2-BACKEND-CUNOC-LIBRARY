package com.hrp.libreriacunocbackend.controllers.borrow;

import com.hrp.libreriacunocbackend.dto.reservation.ReservationResponseDTO;
import com.hrp.libreriacunocbackend.dto.reservation.ReservationResponseRangeDTO;
import com.hrp.libreriacunocbackend.dto.reservation.ReservatonRequestDTO;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.EntityNotFoundException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.service.reservation.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/v1/reservation")
public class ReservationController {
    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    @GetMapping("get_range/{startIndex}/{endIndex}")
    public ResponseEntity<List<ReservationResponseRangeDTO>> getByRange(@PathVariable(name = "startIndex") Integer startIndex, @PathVariable(name = "endIndex") Integer endIndex) throws NotAcceptableException, BadRequestException {
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.getByRange(startIndex, endIndex));
    }

    @PostMapping("create")
    @PreAuthorize("hasRole('LIBRARIAN') OR hasRole('STUDENT')")
    public ResponseEntity<ReservationResponseDTO> create(@RequestBody ReservatonRequestDTO reservatonRequestDTO) throws NotAcceptableException, EntityNotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.create(reservatonRequestDTO));
    }


}
