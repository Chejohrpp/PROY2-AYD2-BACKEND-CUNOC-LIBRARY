package com.hrp.libreriacunocbackend.service.reservation.tasks;

import com.hrp.libreriacunocbackend.entities.Reservation;
import com.hrp.libreriacunocbackend.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationExpirationTaskTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationExpirationTask reservationExpirationTask;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void checkReservationExpiry_shouldDeactivateExpiredReservations() {
        // Datos de ejemplo
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Reservation reservation1 = new Reservation();
        reservation1.setDate(yesterday.minusDays(1));
        reservation1.setIsActivate(true);

        Reservation reservation2 = new Reservation();
        reservation2.setDate(yesterday.minusDays(2));
        reservation2.setIsActivate(true);

        List<Reservation> reservations = Arrays.asList(reservation1, reservation2);

        when(reservationRepository.findByDateBeforeAndIsActivate(yesterday, true)).thenReturn(reservations);

        // Llamada al método
        reservationExpirationTask.checkReservationExpiry();

        // Verificaciones
        verify(reservationRepository, times(1)).findByDateBeforeAndIsActivate(yesterday, true);
        verify(reservationRepository, times(1)).save(reservation1);
        verify(reservationRepository, times(1)).save(reservation2);
        assertFalse(reservation1.getIsActivate());
        assertFalse(reservation2.getIsActivate());
    }

    @Test
    void checkReservationExpiry_noExpiredReservations() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        when(reservationRepository.findByDateBeforeAndIsActivate(yesterday, true)).thenReturn(List.of());

        // Llamada al método
        reservationExpirationTask.checkReservationExpiry();

        // Verificaciones
        verify(reservationRepository, times(1)).findByDateBeforeAndIsActivate(yesterday, true);
        verify(reservationRepository, times(0)).save(any(Reservation.class));
    }

    @Test
    void checkReservationExpiry_handlesExceptionGracefully() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Reservation reservation = new Reservation();
        reservation.setDate(yesterday.minusDays(1));
        reservation.setIsActivate(true);

        when(reservationRepository.findByDateBeforeAndIsActivate(yesterday, true)).thenReturn(Arrays.asList(reservation));
        doThrow(new RuntimeException("Database error")).when(reservationRepository).save(reservation);

        // Llamada al método
        reservationExpirationTask.checkReservationExpiry();

        // Verificaciones
        verify(reservationRepository, times(1)).findByDateBeforeAndIsActivate(yesterday, true);
        verify(reservationRepository, times(1)).save(reservation);
    }
}