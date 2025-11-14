package kr.hhplus.be.server.domain.concert.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SeatJpaRepository extends JpaRepository<SeatEntity, String> {

    @EntityGraph(attributePaths = {"schedule", "schedule.concert"})
    Optional<SeatEntity> findById(String id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s " +
            "FROM SeatEntity s " +
            "WHERE s.id = :id")
    Optional<SeatEntity> findByIdForUpdate(@Param("id") String id);
}
