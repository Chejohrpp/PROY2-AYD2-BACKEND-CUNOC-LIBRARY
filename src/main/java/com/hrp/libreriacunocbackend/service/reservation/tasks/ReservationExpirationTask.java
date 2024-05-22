package com.hrp.libreriacunocbackend.service.reservation.tasks;

import com.hrp.libreriacunocbackend.entities.Reservation;
import com.hrp.libreriacunocbackend.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ReservationExpirationTask {

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationExpirationTask(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // Este método se ejecutará diariamente
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void checkReservationExpiry() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Reservation> reservations = reservationRepository.findByDateBeforeAndIsActivate(yesterday, true);
        for (Reservation reservation : reservations) {
            try {
                // Desactivar la reserva si ha pasado más de un día desde la fecha de reserva
                reservation.setIsActivate(false);
                reservationRepository.save(reservation);
            } catch (Exception e) {
                // Manejo de errores específico si es necesario
                // Por ejemplo, registrar el error o enviar una notificación
                e.printStackTrace();
            }
        }
    }
}
