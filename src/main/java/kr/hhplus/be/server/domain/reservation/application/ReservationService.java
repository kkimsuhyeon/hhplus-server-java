package kr.hhplus.be.server.domain.reservation.application;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.reservation.exception.ReservationErrorCode;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
    public Reservation create(Reservation reservation) {
        repository.findByUserIdAndSeatId(reservation.getUserId(), reservation.getSeatId())
                .ifPresent(result -> {
                    log.error("이미 존재하는 예약 reservationId={}, seatId={}, userId={}", result.getId(), result.getSeatId(), result.getUserId());
                    throw new BusinessException(ReservationErrorCode.ALREADY_RESERVED);
                });

        return save(reservation);
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
