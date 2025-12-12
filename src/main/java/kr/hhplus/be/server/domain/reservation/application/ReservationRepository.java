package kr.hhplus.be.server.domain.reservation.application;

import java.util.Optional;

import kr.hhplus.be.server.domain.reservation.model.Reservation;

public interface ReservationRepository {

    Optional<Reservation> findById(String id);

    Optional<Reservation> findByIdForUpdate(String id);

    Optional<Reservation> findByUserIdAndSeatId(String userId, String seatId);

    Reservation save(Reservation reservation);

    Reservation update(Reservation reservation);
}
