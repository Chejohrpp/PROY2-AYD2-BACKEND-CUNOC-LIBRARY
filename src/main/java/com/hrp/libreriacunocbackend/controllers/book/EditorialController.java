package com.hrp.libreriacunocbackend.controllers.book;

import com.hrp.libreriacunocbackend.dto.editorial.EditorialRequestDTO;
import com.hrp.libreriacunocbackend.dto.editorial.EditorialResponseDTO;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.service.editorial.EditorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@PreAuthorize("hasRole('LIBRARIAN')")
@RequestMapping("/v1/editorial")
public class EditorialController {
    private final EditorialService editorialService;

    @Autowired
    public EditorialController(EditorialService editorialService) {
        this.editorialService = editorialService;
    }

    @PostMapping("create")
    public ResponseEntity<EditorialResponseDTO> create(@RequestBody EditorialRequestDTO editorialRequestDTO) throws NotAcceptableException {
        return ResponseEntity.status(HttpStatus.CREATED).body(editorialService.create(editorialRequestDTO));
    }

    @GetMapping("get_name_filter")
    public ResponseEntity<List<EditorialResponseDTO>> getByNameFilter(@RequestParam(name = "filter") String filter) throws BadRequestException {
        return ResponseEntity.status(HttpStatus.OK).body(editorialService.getByFilter(filter));
    }
}
