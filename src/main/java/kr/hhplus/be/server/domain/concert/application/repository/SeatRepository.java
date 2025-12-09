package kr.hhplus.be.server.domain.concert.application.repository;

import kr.hhplus.be.server.domain.concert.model.Seat;

import java.util.List;
import java.util.Optional;

public interface SeatRepository {

    Optional<Seat> findById(String id);

    Optional<Seat> findByIdForUpdate(String id);

    List<Seat> findByScheduleId(String scheduleId);

    Seat save(Seat seat);

    Seat update(Seat seat);
}
