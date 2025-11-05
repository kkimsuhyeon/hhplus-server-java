package kr.hhplus.be.server.domain.concert.model.repository;

import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;

import java.util.Optional;

public interface SeatRepository {

    public Optional<SeatEntity> findById(String id);
}
