package com.hrp.libreriacunocbackend.controllers.report;

import com.hrp.libreriacunocbackend.dto.book.BookResponseDTO;
import com.hrp.libreriacunocbackend.dto.borrow.BorrowResponseCompleteDTO;
import com.hrp.libreriacunocbackend.dto.report.BorrowedCareerIntervalResponseDTO;
import com.hrp.libreriacunocbackend.dto.report.LateFeeBorrowsStudentIntervalResponseDTO;
import com.hrp.libreriacunocbackend.dto.report.StudentMostBorrowIntervalResponseDTO;
import com.hrp.libreriacunocbackend.dto.report.TotalRevenueIntervalResponseDTO;
import com.hrp.libreriacunocbackend.dto.student.StudentResponseDTO;
import com.hrp.libreriacunocbackend.service.book.BookService;
import com.hrp.libreriacunocbackend.service.borrow.BorrowService;
import com.hrp.libreriacunocbackend.service.student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@PreAuthorize("hasRole('LIBRARIAN')")
@RequestMapping("/v1/report")
public class ReportsController {
    private final BookService bookService;
    private final StudentService studentService;
    private final BorrowService borrowService;

    @Autowired
    public ReportsController(BookService bookService, StudentService service, BorrowService borrowService) {
        this.bookService = bookService;
        this.studentService = service;
        this.borrowService = borrowService;
    }

    @GetMapping("borrow_returned_today")
    public ResponseEntity<List<BorrowResponseCompleteDTO>> getBorrowsToBeReturnedToday(
            @RequestParam(value = "date", required = false) LocalDate date) {
        return ResponseEntity.status(HttpStatus.OK).body(borrowService.getBorrowsToBeReturnedToday(date));
    }

    @GetMapping("borrows_late")
    public ResponseEntity<List<BorrowResponseCompleteDTO>> getLateBorrows(){
        return ResponseEntity.status(HttpStatus.OK).body(borrowService.getLateBorrows());
    }

    @GetMapping("borrow_returned_on_date")
    public ResponseEntity<List<BorrowResponseCompleteDTO>> getBorrowsToBeReturnedOnDate(
            @RequestParam(value = "date", required = false) LocalDate date) {
        return ResponseEntity.status(HttpStatus.OK).body(borrowService.getBorrowsToBeReturnedOnDate(date));
    }

    @GetMapping("borrow_revenue_interval")
    public ResponseEntity<TotalRevenueIntervalResponseDTO> getTotalRevenueInInterval(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.status(HttpStatus.OK).body(borrowService.getTotalRevenueInInterval(startDate, endDate));
    }

    @GetMapping("borrow_career_interval")
    public ResponseEntity<BorrowedCareerIntervalResponseDTO> getMostBorrowedCareerInInterval(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(borrowService.getMostBorrowedCareerInInterval(startDate, endDate));
    }

    @GetMapping("late_fee_borrow_student_interval")
    public ResponseEntity<LateFeeBorrowsStudentIntervalResponseDTO> getLateFeesAndLateBorrowsByStudentAndInterval(
            @RequestParam("idStudent") Long idStudent,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){
        return ResponseEntity.status(HttpStatus.OK).body(borrowService.getLateFeesAndLateBorrowsByStudentAndInterval(idStudent, startDate, endDate));
    }

    @GetMapping("student_most_borrow_interval")
    public ResponseEntity<StudentMostBorrowIntervalResponseDTO> getStudentWithMostBorrowsInInterval(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){
        return ResponseEntity.status(HttpStatus.OK).body(borrowService.getStudentWithMostBorrowsInInterval(startDate, endDate));
    }

    @GetMapping("borrow_student")
    public ResponseEntity<List<BorrowResponseCompleteDTO>> getCurrentBorrowsByStudent(
            @RequestParam("idStudent") Long idStudent
    ){
        return ResponseEntity.status(HttpStatus.OK).body(borrowService.getCurrentBorrowsByStudent(idStudent));
    }

    @GetMapping("books_out_stock")
    public ResponseEntity<List<BookResponseDTO>> getBooksOutOfStock(){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBooksOutOfStock());
    }

    @GetMapping("books_never_borrowed")
    public ResponseEntity<List<BookResponseDTO>> getBooksNeverBorrowed(){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBooksNeverBorrowed());
    }

    @GetMapping("students_penalty")
    public ResponseEntity<List<StudentResponseDTO>> getStudentsInPenalty(){
        return ResponseEntity.status(HttpStatus.OK).body(studentService.getStudentsInPenalty());
    }

}
