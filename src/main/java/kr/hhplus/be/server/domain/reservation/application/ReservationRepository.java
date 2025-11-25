package kr.hhplus.be.server.domain.reservation.application;

import java.util.Optional;

import kr.hhplus.be.server.domain.reservation.model.Reservation;

public interface ReservationRepository {

    Optional<Reservation> findById(String id);

    Reservation save(Reservation reservation);

    Reservation update(Reservation reservation);
}
