package kr.hhplus.be.server.domain.concert.model.repository;

import kr.hhplus.be.server.domain.concert.model.entity.ConcertEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ConcertRepository {

    Page<ConcertEntity> findAllByCriteria(ConcertCriteria criteria, Pageable pageable);

    Optional<ConcertEntity> findById(String concertId);

    ConcertEntity save(ConcertEntity entity);
}
