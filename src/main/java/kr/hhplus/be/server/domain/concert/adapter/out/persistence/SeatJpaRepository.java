package kr.hhplus.be.server.domain.concert.adapter.out.persistence;

import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeatJpaRepository extends JpaRepository<SeatEntity, String> {

    @EntityGraph(attributePaths = {"schedule", "schedule.concert"})
    Optional<SeatEntity> findById(String id);
}
