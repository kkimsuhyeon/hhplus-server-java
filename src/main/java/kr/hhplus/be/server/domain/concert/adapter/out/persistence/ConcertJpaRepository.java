package kr.hhplus.be.server.domain.concert.adapter.out.persistence;

import kr.hhplus.be.server.domain.concert.model.entity.ConcertEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConcertJpaRepository extends JpaRepository<ConcertEntity, String>, JpaSpecificationExecutor<ConcertEntity> {

    Page<ConcertEntity> findAll(Specification<ConcertEntity> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"schedules", "schedules.seats"})
    Optional<ConcertEntity> findByIdWithSchedulesAndSeats(String id);

}
