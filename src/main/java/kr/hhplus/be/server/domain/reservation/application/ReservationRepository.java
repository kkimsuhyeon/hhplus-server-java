package kr.hhplus.be.server.domain.reservation.application;

import kr.hhplus.be.server.domain.reservation.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    Optional<Reservation> findById(String id);

    Optional<Reservation> findByIdWithLock(String id);

    Optional<Reservation> findByUserIdAndSeatId(String userId, String seatId);

    List<Reservation> findByUserId(String userId);

    List<Reservation> findExpiredPendingReservations(LocalDateTime now);

    Reservation save(Reservation reservation);

    Reservation update(Reservation reservation);
}
