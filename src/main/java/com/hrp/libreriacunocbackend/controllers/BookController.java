package com.hrp.libreriacunocbackend.controllers;

import com.hrp.libreriacunocbackend.dto.book.BookRequestAmountCopiesDTO;
import com.hrp.libreriacunocbackend.dto.book.BookRequestAttributeDTO;
import com.hrp.libreriacunocbackend.dto.book.BookRequestDTO;
import com.hrp.libreriacunocbackend.dto.book.BookResponseDTO;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.EntityNotFoundException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.service.book.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Controller
@PreAuthorize("hasRole('LIBRARIAN')")
@RequestMapping("/v1/book")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("getAll")
    public ResponseEntity<List<BookResponseDTO>> getAll(){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getAll());
    }

    @PostMapping("/create")
    public ResponseEntity<BookResponseDTO> create(@RequestBody BookRequestDTO bookRequestDTO) throws NotAcceptableException, EntityNotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(bookRequestDTO));
    }

    @PostMapping("/update_amount_copies")
    public ResponseEntity<BookResponseDTO> updateAmountCopies(@RequestBody BookRequestAmountCopiesDTO bookRequestAmountCopiesDTO) throws NotAcceptableException, EntityNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.UpdateAmountCopies(bookRequestAmountCopiesDTO));
    }

    @GetMapping("get_range/{startIndex}/{endIndex}")
    public ResponseEntity<List<BookResponseDTO>> getByRange(@PathVariable(name = "startIndex") Integer startIndex, @PathVariable(name = "endIndex") Integer endIndex) throws NotAcceptableException, BadRequestException {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getByRange(startIndex, endIndex));
    }

    @GetMapping("get_attribute_filter")
    public ResponseEntity<List<BookResponseDTO>> getByAttribute(@RequestBody BookRequestAttributeDTO bookRequestAttributeDTO) throws BadRequestException {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getByAttribute(bookRequestAttributeDTO));
    }


}
