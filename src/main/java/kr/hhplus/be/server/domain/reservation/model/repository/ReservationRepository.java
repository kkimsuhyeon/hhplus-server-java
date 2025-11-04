package kr.hhplus.be.server.domain.reservation.model.repository;

import kr.hhplus.be.server.domain.reservation.model.entity.ReservationEntity;

public interface ReservationRepository {

    ReservationEntity save(ReservationEntity entity);
}
