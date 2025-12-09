package kr.hhplus.be.server.domain.concert.application.repository;

import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;

import java.util.List;
import java.util.Optional;

public interface ConcertScheduleRepository {

    Optional<ConcertSchedule> findById(String id);

    List<ConcertSchedule> findByConcertId(String concertId);

    ConcertSchedule save(ConcertSchedule schedule);

    ConcertSchedule update(ConcertSchedule schedule);
}
