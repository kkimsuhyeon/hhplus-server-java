package kr.hhplus.be.server.domain.reservation.application;

import kr.hhplus.be.server.domain.reservation.model.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    Optional<Reservation> findById(String id);

    Optional<Reservation> findByIdForUpdate(String id);

    Optional<Reservation> findByUserIdAndSeatId(String userId, String seatId);

    List<Reservation> findByUserId(String userId);

    Reservation save(Reservation reservation);

    Reservation update(Reservation reservation);
}
