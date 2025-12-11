package kr.hhplus.be.server.domain.reservation.application;

import java.util.Optional;

import kr.hhplus.be.server.domain.reservation.model.Reservation;

public interface ReservationRepository {

    Optional<Reservation> findById(String id);

    Optional<Reservation> findByIdForUpdate(String id);

    Reservation save(Reservation reservation);

    Reservation update(Reservation reservation);
}
