package com.hrp.libreriacunocbackend.service.borrow;

import com.hrp.libreriacunocbackend.dto.book.BookRequestAmountCopiesDTO;
import com.hrp.libreriacunocbackend.dto.borrow.*;
import com.hrp.libreriacunocbackend.dto.fee.FeeReportStudentResponseDTO;
import com.hrp.libreriacunocbackend.dto.fee.FeeRequestDTO;
import com.hrp.libreriacunocbackend.dto.fee.FeeResponseDTO;
import com.hrp.libreriacunocbackend.dto.report.BorrowedCareerIntervalResponseDTO;
import com.hrp.libreriacunocbackend.dto.report.LateFeeBorrowsStudentIntervalResponseDTO;
import com.hrp.libreriacunocbackend.dto.report.StudentMostBorrowIntervalResponseDTO;
import com.hrp.libreriacunocbackend.dto.report.TotalRevenueIntervalResponseDTO;
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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private Student validateBorrow(String carnet) throws NotAcceptableException, EntityNotFoundException {
        Student student = studentService.getByCarnet(carnet)
                .orElseThrow(() -> new EntityNotFoundException(String.format("carnet %s not found", carnet)));

        if (!student.getCanBorrow()) {
            throw new NotAcceptableException("the student can't do borrows");
        }

        return student;
    }

    private boolean validateCantBorrowStudent(Student student) throws NotAcceptableException {
        int borrowedBooksCount = borrowRepository.countByStudentAndState(student, State.BORROW);
        borrowedBooksCount += borrowRepository.countByStudentAndState(student, State.LATE);
        borrowedBooksCount += borrowRepository.countByStudentAndState(student, State.LOST);
        if (borrowedBooksCount >= 3) {
            throw new NotAcceptableException("El estudiante ya tiene tres libros prestados.");
        }
        return true;
    }

    private Book validateAmountCopies(String isbn) throws EntityNotFoundException, NotAcceptableException {
       Book book = bookService.getByIsbn(isbn)
                .orElseThrow(() -> new EntityNotFoundException(String.format("book %s not found", isbn)));

        if (book.getAmount() <= 0) {
            throw new NotAcceptableException("No hay copias disponibles del libro deseado.");
        }
        return book;
    }

    private void updateCopiesBook(String isbn, Integer newCopies) throws NotAcceptableException, EntityNotFoundException {
        BookRequestAmountCopiesDTO bookRequestAmountCopiesDTO = new BookRequestAmountCopiesDTO(isbn, newCopies);
        bookService.UpdateAmountCopies(bookRequestAmountCopiesDTO);
    }

    @Override
    @Transactional
    public BorrowResponseDTO newBorrow(BorrowRequestDTO borrowRequestDTO) throws NotAcceptableException, EntityNotFoundException {
        //Paso 0: validar los estados
        updateBorrowStatus(borrowRepository.findAll());

        // Paso 1: Verificar si el estudiante tiene permitido realizar préstamos
        Student student = validateBorrow(borrowRequestDTO.getCarnet());

        // Paso 2: Verificar si el estudiante ya tiene tres libros prestados
        validateCantBorrowStudent(student);

        // Paso 3: Verificar si hay copias disponibles del libro deseado
        Book book = validateAmountCopies(borrowRequestDTO.getIsbn());

        //Paso 4: actualizar las copies del libro
        updateCopiesBook(borrowRequestDTO.getIsbn(), -1);

        Borrow borrow = new Borrow();
        borrow.setDateBorrow(LocalDate.from(borrowRequestDTO.getDateBorrow()));
        borrow.setBook(book);
        borrow.setStudent(student);
        borrow.setPriceBorrow(book.getPrice());
        borrow.setState(State.BORROW);

        borrow = borrowRepository.save(borrow);
        return new BorrowResponseDTO(borrow);
    }

    @Override
    public BorrowResponseUpdateStateDTO updateState(BorrowRequestUpdateStateDTO borrowRequestUpdateStateDTO) throws EntityNotFoundException {
        // Encontrar el préstamo basado en el id proporcionado
        Borrow borrow = getById(borrowRequestUpdateStateDTO.getIdBorrow());

        // Actualizar el estado del préstamo
        borrow.setState(borrowRequestUpdateStateDTO.getStatus());
        borrowRepository.save(borrow);

        // Devolver la respuesta
        return new BorrowResponseUpdateStateDTO(borrow);
    }

    @Override
    public BorrowResponseFeaturesFeeDTO findBorrow(Long id) throws EntityNotFoundException, NotAcceptableException {
        //Paso 0: validar los estados
        updateBorrowStatus(borrowRepository.findAll());

        // Encontrar el préstamo basado en el id proporcionado
        Borrow borrow = getById(id);

        if (borrow.getState() == State.RETURN){
            return new BorrowResponseFeaturesFeeDTO(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        // Calcular las tarifas
        Map<String, Double> fees = calculateTotalFee(borrow, LocalDateTime.now());

        // Obtener el título del libro y el nombre del estudiante
        String bookTitle = borrow.getBook().getTitle();
        String studentName = borrow.getStudent().getName();

        // Construir y devolver el DTO de respuesta
        return new BorrowResponseFeaturesFeeDTO(
                borrow.getIdBorrow(),
                studentName,
                bookTitle,
                borrow.getDateBorrow(),
                fees.get("lateFee"),
                fees.get("penalty"),
                fees.get("fee")
        );
    }


    private Borrow getById(Long id) throws EntityNotFoundException {
        // Encontrar el préstamo basado en el id proporcionado
        return borrowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Préstamo no encontrado con ID: " + id));
    }

    private void validateStateLost(Borrow borrow){
        // Verificar si el libro está atrasado y actualizar su estado
        if (borrow.getState() == State.LOST) {
            // Actualizar el estado del estudiante a poder prestar libros
            Student student = borrow.getStudent();
            student.setCanBorrow(true);
            studentService.updateStudentStudent(student);
        }
    }


    @Transactional
    @Override
    public BorrowResponseFeeDTO returnBook(BorrowRequestFeeDTO borrowRequestFeeDTO) throws EntityNotFoundException, NotAcceptableException {
        // Encontrar el préstamo basado en el id proporcionado
        Borrow borrow = getById(borrowRequestFeeDTO.getIdBorrow());

        validateStateLost(borrow);

        // Crear una instancia de Fee para registrar el pago
        Double lateFee =  borrowRequestFeeDTO.getLateFee() + borrowRequestFeeDTO.getPenalty();
        FeeRequestDTO feeRequestDTO = new FeeRequestDTO(borrow, borrowRequestFeeDTO.getDate(), borrowRequestFeeDTO.getRegularFee(), lateFee );
        FeeResponseDTO feeResponseDTO = feeService.create(feeRequestDTO);

        //devolver la cantidad del libro
        updateCopiesBook(borrow.getBook().getIsbn(), 1);

        // Actualizar el estado del préstamo a devuelto
        BorrowResponseUpdateStateDTO borrowResponseUpdateStateDTO = updateStateBorrowRequest(borrow.getIdBorrow(), State.RETURN);

        return new BorrowResponseFeeDTO(borrowResponseUpdateStateDTO);
    }

    private BorrowResponseUpdateStateDTO updateStateBorrowRequest(Long id, State state) throws EntityNotFoundException {
        BorrowRequestUpdateStateDTO borrowRequestUpdateStateDTO = new BorrowRequestUpdateStateDTO(id, state);
        return updateState(borrowRequestUpdateStateDTO);
    }

    @Override
    public BorrowedCareerIntervalResponseDTO getMostBorrowedCareerInInterval(LocalDate startDate, LocalDate endDate) {
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

        List<Borrow> borrowsOfStudentsInMostBorrowedCareer = borrowRepository.findBorrowsByCareerInInterval(mostBorrowedCareer.getIdCareer(), startDate, endDate);
        return new BorrowedCareerIntervalResponseDTO(mostBorrowedCareer, maxBorrows,  getListBorrowsResponse(borrowsOfStudentsInMostBorrowedCareer));
    }

    private List<BorrowResponseCompleteDTO> getListBorrowsResponse(List<Borrow> borrows){
        return borrows
                .stream()
                .map(BorrowResponseCompleteDTO ::new)
                .collect(Collectors.toList());
    }

    @Override
    public LateFeeBorrowsStudentIntervalResponseDTO getLateFeesAndLateBorrowsByStudentAndInterval(Long studentId, LocalDate startDate, LocalDate endDate) {
        List<Fee> lateFees = feeService.findLateFeesByStudentAndInterval(studentId, startDate, endDate);
        List<Borrow> lateBorrows = borrowRepository.findLateBorrowsByStudentAndInterval(studentId, startDate, endDate);
        return new LateFeeBorrowsStudentIntervalResponseDTO(getFeeReportsResponse(lateFees), getListBorrowsResponse(lateBorrows));
    }

    private List<FeeReportStudentResponseDTO> getFeeReportsResponse(List<Fee> fees){
        return fees
                .stream()
                .map(FeeReportStudentResponseDTO ::new)
                .collect(Collectors.toList());
    }

    @Override
    public StudentMostBorrowIntervalResponseDTO getStudentWithMostBorrowsInInterval(LocalDate startDate, LocalDate endDate) {
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

        List<Borrow> borrowsByStudentWithMostBorrows = borrowRepository.findBorrowsByStudentAndInterval(studentWithMostBorrows.getUserId(), startDate, endDate);
        return new StudentMostBorrowIntervalResponseDTO(studentWithMostBorrows.getName(), getListBorrowsResponse(borrowsByStudentWithMostBorrows));
    }

    @Override
    public List<Borrow> getActivateBorrowsByStudent(Long studentId) {
        return borrowRepository.findActiveBorrowsByStudent(studentId);
    }

    @Override
    public List<BorrowResponseCompleteDTO> getCurrentBorrowsByStudent(Long studentId){
        return borrowRepository.findCurrentBorrowsByStudent(studentId)
                .stream()
                .map(BorrowResponseCompleteDTO :: new)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void updateBorrowStatus(List<Borrow> borrows) {
        LocalDate currentDate = LocalDate.now();
        for (Borrow borrow : borrows) {
            if (borrow == null || borrow.getDateBorrow() == null || borrow.getState() == null) {
                throw new IllegalArgumentException("Borrow, DateBorrow, and State must not be null");
            }
            if (borrow.getState() == State.BORROW) {
                LocalDate dueDate = borrow.getDateBorrow().plusDays(3); // Fecha límite de devolución: 3 días después del préstamo
                if (currentDate.isAfter(dueDate) && borrow.getState() != State.LATE) {
                    // El libro está atrasado
                    borrow.setState(State.LATE);
                    borrowRepository.save(borrow);
                }

                LocalDate lostDate = borrow.getDateBorrow().plusMonths(1); // Fecha límite para considerar el libro como perdido: 1 mes después del préstamo
                if (currentDate.isAfter(lostDate) && borrow.getState() != State.LOST) {
                    // El libro se considera perdido
                    borrow.setState(State.LOST);
                    borrowRepository.save(borrow);
                    // Actualizar el estado del estudiante a no poder prestar libros
                    Student student = borrow.getStudent();
                    if (student != null) {
                        student.setCanBorrow(false);
                        studentService.updateStudentStudent(student);
                    } else {
                        throw new IllegalStateException("Borrow's student must not be null");
                    }
                }
            }
        }
    }


    @Override
    public double calculatePenaltyAmount(Long studentId) throws NotAcceptableException {
        List<Borrow> borrows = borrowRepository.findCurrentBorrowsByStudent(studentId);
        double totalPenaltyAmount = 0.0;

        for (Borrow borrow : borrows) {
            Map<String, Double> fees = calculateTotalFee(borrow, LocalDateTime.now());
            totalPenaltyAmount += fees.get("lateFee") + fees.get("penalty"); // Sumamos moras y penalizaciones
        }

        return totalPenaltyAmount;
    }


    @Override
    public long count() {
        return borrowRepository.count();
    }

    @Override
    @Transactional
    public List<BorrowResponseCompleteDTO> getAll(){
        //before validate all borrows
        updateBorrowStatus(borrowRepository.findAll());
        return borrowRepository.findAll()
                .stream()
                .map(BorrowResponseCompleteDTO:: new)
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowResponseCompleteDTO> getLateBorrows(){
        updateBorrowStatus(borrowRepository.findAll());
        return borrowRepository.findLateBorrows()
                .stream()
                .map(BorrowResponseCompleteDTO :: new)
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowResponseCompleteDTO> getBorrowsToBeReturnedToday(LocalDate date){
        updateBorrowStatus(borrowRepository.findAll());
        return borrowRepository.findBorrowsToBeReturnedToday(date)
                .stream()
                .map(BorrowResponseCompleteDTO :: new)
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowResponseCompleteDTO> getBorrowsToBeReturnedOnDate(LocalDate date){
        updateBorrowStatus(borrowRepository.findAll());
        return borrowRepository.findBorrowsToBeReturnedOnDate(date)
                .stream()
                .map(BorrowResponseCompleteDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public TotalRevenueIntervalResponseDTO getTotalRevenueInInterval(LocalDate startDate, LocalDate endDate){
        double totalRevenue = borrowRepository.findTotalRevenueInInterval(startDate, endDate);
        List<BorrowResponseCompleteDTO> borrrows = borrowRepository.findBorrowsInInterval(startDate, endDate)
                .stream()
                .map(BorrowResponseCompleteDTO::new)
                .toList();
        return new TotalRevenueIntervalResponseDTO(totalRevenue, borrrows);
    }





    private Map<String, Double> calculateTotalFee(Borrow borrow, LocalDateTime returnDate) throws NotAcceptableException {
        LocalDate borrowDate = borrow.getDateBorrow();
        long daysBorrowed = ChronoUnit.DAYS.between(borrowDate, returnDate.toLocalDate());
        long daysAllowed = 3; // La cantidad máxima de días permitidos para el préstamo

        double regularFee = Math.min(daysBorrowed, daysAllowed) * 5.0; // Q5.00 por día de préstamo permitido
        double lateFee = 0.0;
        double penalty = 0.0;

        if (daysBorrowed > daysAllowed) {
            long lateDays = daysBorrowed - daysAllowed;
            lateFee = lateDays * 15.0; // Q15.00 por día de atraso

            if (daysBorrowed > 28 && borrow.getState() == State.LOST) {
                // Aplicar sanción por libro perdido después de un mes
                penalty = borrow.getPriceBorrow() + 150.0;
            }
        }

        Map<String, Double> fees = new HashMap<>();
        fees.put("fee", regularFee);
        fees.put("lateFee", lateFee);
        fees.put("penalty", penalty);
        return fees;
    }
}
