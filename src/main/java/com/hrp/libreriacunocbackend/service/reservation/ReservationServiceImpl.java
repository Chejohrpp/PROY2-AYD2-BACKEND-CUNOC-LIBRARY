package com.hrp.libreriacunocbackend.service.reservation;

import com.hrp.libreriacunocbackend.dto.reservation.ReservationResponseDTO;
import com.hrp.libreriacunocbackend.dto.reservation.ReservationResponseRangeDTO;
import com.hrp.libreriacunocbackend.dto.reservation.ReservatonRequestDTO;
import com.hrp.libreriacunocbackend.entities.Reservation;
import com.hrp.libreriacunocbackend.entities.book.Book;
import com.hrp.libreriacunocbackend.entities.user.Student;
import com.hrp.libreriacunocbackend.exceptions.BadRequestException;
import com.hrp.libreriacunocbackend.exceptions.EntityNotFoundException;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.repository.ReservationRepository;
import com.hrp.libreriacunocbackend.service.book.BookService;
import com.hrp.libreriacunocbackend.service.student.StudentService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService{
    private final ReservationRepository reservationRepository;
    private final StudentService studentService;
    private final BookService bookService;


    public ReservationServiceImpl(ReservationRepository reservationRepository, StudentService studentService, BookService bookService) {
        this.reservationRepository = reservationRepository;
        this.studentService = studentService;
        this.bookService = bookService;
    }

    @Override
    public ReservationResponseDTO create(ReservatonRequestDTO reservatonRequestDTO) throws NotAcceptableException, EntityNotFoundException {
        // Obtener el estudiante y el libro basados en los IDs proporcionados
        Student student =studentService.getByUsername(reservatonRequestDTO.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado con username: " + reservatonRequestDTO.getUsername()));
        Book book = bookService.getById(reservatonRequestDTO.getIdBook())
                .orElseThrow(() -> new NotAcceptableException("Libro no encontrado con ID: " + reservatonRequestDTO.getIdBook()));

        // Verificar si el libro está disponible para reservar
        if (book.getAmount() > 0) {
            throw new NotAcceptableException("El libro ya está disponible para préstamo, no se puede reservar.");
        }

        // Crear la reserva
        Reservation reservation = new Reservation();
        reservation.setDate(LocalDate.from(reservatonRequestDTO.getDate()));
        reservation.setStudent(student);
        reservation.setBook(book);
        reservation.setIsActivate(true);
        reservationRepository.save(reservation);

        return new ReservationResponseDTO(reservation.getDate().toString(), student.getUserId(), book.getIdBook(), true);
    }

    @Override
    public List<ReservationResponseRangeDTO> getByRange(Integer startIndex, Integer endIndex) throws NotAcceptableException, BadRequestException {
        if (startIndex == null || endIndex == null) {
            throw new NotAcceptableException("Indices no pueden ser nulos");
        }
        if (startIndex < 0 || endIndex < 0) {
            throw new NotAcceptableException("Indices no pueden ser negativos");
        }
        if (startIndex > endIndex) {
            throw new BadRequestException("El índice de inicio no puede ser mayor que el índice de fin");
        }
        return reservationRepository.findByRange(startIndex, endIndex)
                .stream()
                .map(ReservationResponseRangeDTO :: new)
                .collect(Collectors.toList());
    }
}
