package com.hrp.libreriacunocbackend.controllers.users;

import com.hrp.libreriacunocbackend.dto.career.CareerRequestDTO;
import com.hrp.libreriacunocbackend.dto.career.CareerResponseDTO;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.DuplicatedEntityException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.service.career.CareerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@PreAuthorize("hasRole('LIBRARIAN')")
@RequestMapping("/v1/career")
public class CareerController {

    private final CareerService careerService;

    @Autowired
    public CareerController(CareerService careerService) {
        this.careerService = careerService;
    }

    @PostMapping("create")
    public ResponseEntity<CareerResponseDTO> createCareer(@RequestBody CareerRequestDTO careerRequestDTO) throws NotAcceptableException, DuplicatedEntityException {
        return ResponseEntity.status(HttpStatus.CREATED).body(careerService.createCareer(careerRequestDTO));
    }

    @GetMapping("get_name_filter")
    public ResponseEntity<List<CareerResponseDTO>> getByNameFilter(@RequestParam String filter) throws BadRequestException {
        return ResponseEntity.status(HttpStatus.OK).body(careerService.getByFilter(filter));
    }
}
