package kr.hhplus.be.server.domain.concert.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConcertScheduleJpaRepository extends JpaRepository<ConcertScheduleJpaEntity, String> {

    List<ConcertScheduleJpaEntity> findByConcertId(String concertId);
}
