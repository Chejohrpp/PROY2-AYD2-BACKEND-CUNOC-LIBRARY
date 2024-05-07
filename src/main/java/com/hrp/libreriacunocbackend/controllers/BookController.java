package com.hrp.libreriacunocbackend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@PreAuthorize("hasRole('LIBRARIAN')")
@RequestMapping("/v1/book")
public class BookController {

    @GetMapping("getAll")
    public ResponseEntity<String> getAll(){
        return ResponseEntity.status(HttpStatus.OK).body("something");
    }

}
