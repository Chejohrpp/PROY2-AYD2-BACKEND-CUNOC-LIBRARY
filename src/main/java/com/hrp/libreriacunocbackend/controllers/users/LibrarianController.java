package com.hrp.libreriacunocbackend.controllers.users;

import com.hrp.libreriacunocbackend.dto.librarian.LibrarianRequestDTO;
import com.hrp.libreriacunocbackend.dto.librarian.LibrarianResponseDTO;
import com.hrp.libreriacunocbackend.entities.user.User;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/librarian")
public class LibrarianController {

    private final UserService userService;

    @Autowired
    public LibrarianController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<LibrarianResponseDTO> createLibrarian(@RequestBody LibrarianRequestDTO librarianRequestDTO) throws NotAcceptableException {
         return ResponseEntity.status(HttpStatus.CREATED).body(userService.createLibrarian(librarianRequestDTO));
    }
}
