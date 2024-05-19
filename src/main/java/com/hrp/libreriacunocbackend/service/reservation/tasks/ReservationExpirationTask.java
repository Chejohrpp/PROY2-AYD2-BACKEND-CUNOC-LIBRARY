package com.hrp.libreriacunocbackend.service.reservation.tasks;

import com.hrp.libreriacunocbackend.entities.Reservation;
import com.hrp.libreriacunocbackend.repository.ReservationRepository;
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
    public void checkReservationExpiry() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Reservation> reservations = reservationRepository.findByDateBeforeAndIsActivate(yesterday, true);
        for (Reservation reservation : reservations) {
            // Desactivar la reserva si ha pasado más de un día desde la fecha de reserva
            reservation.setIsActivate(false);
            reservationRepository.save(reservation);
        }
    }


}
