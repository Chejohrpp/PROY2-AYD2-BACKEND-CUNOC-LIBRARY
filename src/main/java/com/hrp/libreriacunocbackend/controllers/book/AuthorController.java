package com.hrp.libreriacunocbackend.controllers.book;

import com.hrp.libreriacunocbackend.dto.author.AuthorRequestDTO;
import com.hrp.libreriacunocbackend.dto.author.AuthorResponseDTO;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.DuplicatedEntityException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.service.author.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@PreAuthorize("hasRole('LIBRARIAN')")
@RequestMapping("/v1/author")
public class AuthorController {
    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping("create")
    public ResponseEntity<AuthorResponseDTO> create(@RequestBody AuthorRequestDTO authorRequestDTO) throws NotAcceptableException, DuplicatedEntityException {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.createAuthor(authorRequestDTO));
    }

    @GetMapping("get_name_filter")
    public ResponseEntity<List<AuthorResponseDTO>> getByNameFilter(@RequestParam(name = "filter") String filter) throws BadRequestException {
        return ResponseEntity.status(HttpStatus.OK).body(authorService.getByNameFilter(filter));
    }

}
