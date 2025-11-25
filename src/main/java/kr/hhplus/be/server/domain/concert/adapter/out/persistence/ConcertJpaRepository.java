package kr.hhplus.be.server.domain.concert.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcertJpaRepository extends JpaRepository<ConcertJpaEntity, String>, JpaSpecificationExecutor<ConcertJpaEntity> {

}
