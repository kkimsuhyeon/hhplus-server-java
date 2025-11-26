package kr.hhplus.be.server.domain.reservation.application;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.reservation.exception.ReservationErrorCode;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository repository;

    @Transactional(readOnly = true)
    public Reservation getReservation(String reservationId) {
        return repository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ReservationErrorCode.NOT_FOUND));
    }

    @Transactional
    public Reservation save(Reservation reservation) {
        return repository.save(reservation);
    }

    @Transactional
    public Reservation update(Reservation reservation) {
        return repository.update(reservation);
    }
}
