package kr.hhplus.be.server.domain.reservation.model.repository;

import kr.hhplus.be.server.domain.reservation.model.entity.ReservationEntity;

import java.util.Optional;

public interface ReservationRepository {

    Optional<ReservationEntity> findById(String id);

    ReservationEntity save(ReservationEntity entity);
}
