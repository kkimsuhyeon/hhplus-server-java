package kr.hhplus.be.server.domain.concert.model.repository;

import kr.hhplus.be.server.domain.concert.model.entity.ConcertEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConcertRepository {

    Page<ConcertEntity> findAllByCriteria(ConcertCriteria criteria, Pageable pageable);

    ConcertEntity save(ConcertEntity entity);
}
