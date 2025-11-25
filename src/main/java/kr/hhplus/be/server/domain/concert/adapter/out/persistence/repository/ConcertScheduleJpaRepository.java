package kr.hhplus.be.server.domain.concert.adapter.out.persistence.repository;

import kr.hhplus.be.server.domain.concert.adapter.out.persistence.entity.ConcertScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConcertScheduleJpaRepository extends JpaRepository<ConcertScheduleEntity, String> {

    List<ConcertScheduleEntity> findByConcertId(String concertId);
}
