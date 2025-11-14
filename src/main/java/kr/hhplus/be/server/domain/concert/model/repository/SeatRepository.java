package kr.hhplus.be.server.domain.concert.model.repository;

import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;

import java.util.Optional;

public interface SeatRepository {

    Optional<SeatEntity> findById(String id);

    Optional<SeatEntity> findByIdForUpdate(String id);
}
