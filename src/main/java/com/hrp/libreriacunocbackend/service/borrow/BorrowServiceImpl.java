package com.hrp.libreriacunocbackend.service.borrow;

import com.hrp.libreriacunocbackend.dto.borrow.*;
import com.hrp.libreriacunocbackend.dto.fee.FeeRequestDTO;
import com.hrp.libreriacunocbackend.dto.fee.FeeResponseDTO;
import com.hrp.libreriacunocbackend.entities.Borrow;
import com.hrp.libreriacunocbackend.entities.Fee;
import com.hrp.libreriacunocbackend.entities.book.Book;
import com.hrp.libreriacunocbackend.entities.user.Career;
import com.hrp.libreriacunocbackend.entities.user.Student;
import com.hrp.libreriacunocbackend.enums.borrow.State;
import com.hrp.libreriacunocbackend.exceptions.EntityNotFoundException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.repository.BorrowRepository;
import com.hrp.libreriacunocbackend.service.book.BookService;
import com.hrp.libreriacunocbackend.service.career.CareerService;
import com.hrp.libreriacunocbackend.service.fee.FeeService;
import com.hrp.libreriacunocbackend.service.student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BorrowServiceImpl implements BorrowService{

    private final BorrowRepository borrowRepository;
    private final StudentService studentService;
    private final BookService bookService;
    private final FeeService feeService;
    private final CareerService careerService;

    @Autowired
    public BorrowServiceImpl(BorrowRepository borrowRepository, StudentService studentService, BookService bookService, FeeService feeService, CareerService careerService) {
        this.borrowRepository = borrowRepository;
        this.studentService = studentService;
        this.bookService = bookService;
        this.feeService = feeService;
        this.careerService = careerService;
    }

    @Override
    public BorrowResponseDTO newBorrow(BorrowRequestDTO borrowRequestDTO) throws NotAcceptableException, EntityNotFoundException {
        // Paso 1: Verificar si el estudiante tiene permitido realizar préstamos
        Student student = studentService.getByCarnet(borrowRequestDTO.getCarnet())
                .orElseThrow(() -> new EntityNotFoundException(String.format("carnet %s not found", borrowRequestDTO.getCarnet())));
        if (!student.getCanBorrow()) {
            throw new NotAcceptableException("the student can't do borrows");
        }

        // Paso 2: Verificar si el estudiante ya tiene tres libros prestados
        int borrowedBooksCount = borrowRepository.countByStudentAndState(student, State.BORROW);
        if (borrowedBooksCount >= 3) {
            throw new NotAcceptableException("El estudiante ya tiene tres libros prestados.");
        }

        // Paso 3: Verificar si hay copias disponibles del libro deseado
        Book book = bookService.getByIsbn(borrowRequestDTO.getIsbn())
                .orElseThrow(() -> new EntityNotFoundException(String.format("book %s not found", borrowRequestDTO.getIsbn())));

        if (book.getAmount() <= 0) {
            throw new NotAcceptableException("No hay copias disponibles del libro deseado.");
        }

        Borrow borrow = new Borrow();
        borrow.setDateBorrow(LocalDate.from(borrowRequestDTO.getDateBorrow()));
        borrow.setBook(book);
        borrow.setStudent(student);
        borrow.setState(State.BORROW);

        borrow = borrowRepository.save(borrow);
        return new BorrowResponseDTO(borrow);
    }

    @Override
    public BorrowResponseUpdateStateDTO updateState(BorrowRequestUpdateStateDTO borrowRequestUpdateStateDTO) throws EntityNotFoundException {
        // Encontrar el préstamo basado en el id proporcionado
        Borrow borrow = borrowRepository.findById(borrowRequestUpdateStateDTO.getIdBorrow())
                .orElseThrow(() -> new EntityNotFoundException("Préstamo no encontrado con ID: " + borrowRequestUpdateStateDTO.getIdBorrow()));

        // Actualizar el estado del préstamo
        borrow.setState(borrowRequestUpdateStateDTO.getStatus());
        borrowRepository.save(borrow);

        // Devolver la respuesta
        return new BorrowResponseUpdateStateDTO(borrow);
    }



    @Override
    public BorrowResponseFeeDTO returnBook(BorrowRequestFeeDTO borrowRequestFeeDTO) throws EntityNotFoundException, NotAcceptableException {
        // Encontrar el préstamo basado en el id proporcionado
        Borrow borrow = borrowRepository.findById(borrowRequestFeeDTO.getIdBorrow())
                .orElseThrow(() -> new EntityNotFoundException("Préstamo no encontrado con ID: " + borrowRequestFeeDTO.getIdBorrow()));

        // Verificar si el libro está atrasado y actualizar su estado
        if (borrow.getState() == State.LATE) {
            // Actualizar el estado del estudiante a poder prestar libros
            Student student = borrow.getStudent();
            student.setCanBorrow(true);
            studentService.updateStudentStudent(student);
        }

        // Calcular el total a pagar
        double totalFee = calculateTotalFee(borrow, borrowRequestFeeDTO.getDate());

        // Crear una instancia de Fee para registrar el pago
        FeeRequestDTO feeRequestDTO = new FeeRequestDTO(borrow, borrowRequestFeeDTO.getDate(), totalFee, 0.0);
        FeeResponseDTO feeResponseDTO = feeService.create(feeRequestDTO);

        // Actualizar el estado del préstamo a devuelto
        BorrowRequestUpdateStateDTO borrowRequestUpdateStateDTO = new BorrowRequestUpdateStateDTO(borrow.getIdBorrow(), State.RETURN);
        BorrowResponseUpdateStateDTO borrowResponseUpdateStateDTO = updateState(borrowRequestUpdateStateDTO);

        return new BorrowResponseFeeDTO(borrowResponseUpdateStateDTO);
    }

    @Override
    public Map<String, Object> getMostBorrowedCareerInInterval(LocalDate startDate, LocalDate endDate) {
        List<Career> careers = careerService.getAll();

        Career mostBorrowedCareer = null;
        int maxBorrows = 0;

        for (Career career : careers) {
            int borrows = borrowRepository.countBorrowsByCareerInInterval(career.getIdCareer(), startDate, endDate);
            if (borrows > maxBorrows) {
                mostBorrowedCareer = career;
                maxBorrows = borrows;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("mostBorrowedCareer", mostBorrowedCareer);
        result.put("borrowsInMostBorrowedCareer", maxBorrows);
        result.put("borrowsOfStudentsInMostBorrowedCareer", borrowRepository.findBorrowsByCareerInInterval(mostBorrowedCareer.getIdCareer(), startDate, endDate));

        return result;
    }

    @Override
    public Map<String, Object> getLateFeesAndLateBorrowsByStudentAndInterval(Long studentId, LocalDate startDate, LocalDate endDate) {
        List<Fee> lateFees = feeService.findLateFeesByStudentAndInterval(studentId, startDate, endDate);
        List<Borrow> lateBorrows = borrowRepository.findLateBorrowsByStudentAndInterval(studentId, startDate, endDate);

        Map<String, Object> result = new HashMap<>();
        result.put("lateFees", lateFees);
        result.put("lateBorrows", lateBorrows);
        return result;
    }

    @Override
    public Map<String, Object> getStudentWithMostBorrowsInInterval(LocalDate startDate, LocalDate endDate) {
        List<Student> students = studentService.getAll();

        Student studentWithMostBorrows = null;
        int maxBorrows = 0;

        for (Student student : students) {
            int borrows = borrowRepository.countBorrowsByStudentInInterval(student.getUserId(), startDate, endDate);
            if (borrows > maxBorrows) {
                studentWithMostBorrows = student;
                maxBorrows = borrows;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("studentWithMostBorrows", studentWithMostBorrows);
        result.put("borrowsByStudentWithMostBorrows", borrowRepository.findBorrowsByStudentAndInterval(studentWithMostBorrows.getUserId(), startDate, endDate));
        return result;
    }

    @Override
    public List<Borrow> getActivateBorrowsByStudent(Long studentId) {
        return borrowRepository.findActiveBorrowsByStudent(studentId);
    }

    @Override
    public List<Borrow> getCurrentBorrowsByStudent(Long studentId){
        return borrowRepository.findCurrentBorrowsByStudent(studentId);
    }


    @Override
    public void updateBorrowStatus(List<Borrow> borrows) {
        LocalDate currentDate = LocalDate.now();
        for (Borrow borrow : borrows) {
            if (borrow.getState() == State.BORROW) {
                LocalDate dueDate = borrow.getDateBorrow().plusDays(3); // Fecha límite de devolución: 3 días después del préstamo
                if (currentDate.isAfter(dueDate)) {
                    // El libro está atrasado
                    borrow.setState(State.LATE);
                    borrowRepository.save(borrow);
                }

                LocalDate lostDate = borrow.getDateBorrow().plusMonths(1); // Fecha límite para considerar el libro como perdido: 1 mes después del préstamo
                if (currentDate.isAfter(lostDate)) {
                    // El libro se considera perdido
                    borrow.setState(State.LOST);
                    borrowRepository.save(borrow);
                    // Actualizar el estado del estudiante a no poder prestar libros
                    Student student = borrow.getStudent();
                    student.setCanBorrow(false);
                    studentService.updateStudentStudent(student);
                }
            }
        }
    }

    @Override
    public double calculatePenaltyAmount(Long studentId) throws NotAcceptableException {
        List<Borrow> borrows = borrowRepository.findCurrentBorrowsByStudent(studentId);
        double totalPenaltyAmount = 0.0;

        for (Borrow borrow : borrows) {
            totalPenaltyAmount += calculateTotalFee(borrow, LocalDateTime.now());
        }

        return totalPenaltyAmount;
    }

    @Override
    public long count() {
        return borrowRepository.count();
    }


    private double calculateTotalFee(Borrow borrow, LocalDateTime returnDate) throws NotAcceptableException {
        LocalDate borrowDate = borrow.getDateBorrow();
        long daysBorrowed = ChronoUnit.DAYS.between(borrowDate, returnDate.toLocalDate());
        long daysAllowed = 3; // La cantidad máxima de días permitidos para el préstamo

        if (daysBorrowed > daysAllowed) {
            // Se aplica una mora por cada día adicional de retraso
            long lateDays = daysBorrowed - daysAllowed;
            double lateFee = lateDays * 15.0; // Q15.00 por día de atraso
            // Se calcula la cuota por los días reglamentarios de préstamo
            double regularFee = daysAllowed * 5.0; // Q5.00 por día de préstamo
            if (daysBorrowed > 30 && borrow.getState() == State.LOST) {
                // Aplicar sanción por libro perdido después de un mes
                lateFee += borrow.getBook().getPrice() + 150.00;
            }
            // Puedes agregar lógica adicional aquí para calcular otras tarifas (por ejemplo, moras)
            // El total a pagar es la suma de la cuota regular y la mora
            return regularFee + lateFee;
        } else {
            // Se calcula la cuota por los días reglamentarios de préstamo
            return daysBorrowed * 5.0; // Q5.00 por día de préstamo
        }
    }
}
