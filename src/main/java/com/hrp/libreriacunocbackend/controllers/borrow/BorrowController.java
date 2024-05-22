package com.hrp.libreriacunocbackend.controllers.borrow;

import com.hrp.libreriacunocbackend.dto.borrow.*;
import com.hrp.libreriacunocbackend.exceptions.EntityNotFoundException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.service.borrow.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@PreAuthorize("hasRole('LIBRARIAN')")
@RequestMapping("/v1/borrow")
public class BorrowController {


    private  final BorrowService borrowService;

    @Autowired
    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @GetMapping("get_all")
    public ResponseEntity<List<BorrowResponseCompleteDTO>> getAll(){
        return ResponseEntity.status(HttpStatus.OK).body(borrowService.getAll());
    }

    @PostMapping("create")
    public ResponseEntity<BorrowResponseDTO> create(@RequestBody BorrowRequestDTO borrowRequestDTO) throws NotAcceptableException, EntityNotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(borrowService.newBorrow(borrowRequestDTO));
    }

    @GetMapping("find_borrow/{id}")
    public ResponseEntity<BorrowResponseFeaturesFeeDTO> findBorrow(@PathVariable(name = "id") Long id) throws NotAcceptableException, EntityNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(borrowService.findBorrow(id));
    }

    @PostMapping("return")
    public ResponseEntity<BorrowResponseFeeDTO> returnBook(@RequestBody BorrowRequestFeeDTO borrowRequestFeeDTO) throws NotAcceptableException, EntityNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(borrowService.returnBook(borrowRequestFeeDTO));
    }
}
