package com.hrp.libreriacunocbackend.controllers.borrow;

import com.hrp.libreriacunocbackend.dto.borrow.BorrowResponseCompleteDTO;
import com.hrp.libreriacunocbackend.service.borrow.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
