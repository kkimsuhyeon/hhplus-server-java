package kr.hhplus.be.server.domain.reservation.model.service;

import kr.hhplus.be.server.domain.reservation.model.entity.ReservationEntity;

public interface ReservationService {

    ReservationEntity getReservation(String reservationId);
}
