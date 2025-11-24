package kr.hhplus.be.server.domain.concert.adapter.persistence;

import kr.hhplus.be.server.domain.concert.model.entity.ConcertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcertJpaRepository extends JpaRepository<ConcertEntity, String>, JpaSpecificationExecutor<ConcertEntity> {
}
