package kr.hhplus.be.server.domain.concert.application.repository;

import kr.hhplus.be.server.domain.concert.application.dto.criteria.ConcertCriteria;
import kr.hhplus.be.server.domain.concert.model.Concert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ConcertRepository {

    Optional<Concert> findById(String id);

    Page<Concert> findAllByCriteria(ConcertCriteria criteria, Pageable pageable);

    Concert save(Concert concert);

    Concert update(Concert concert);
}
