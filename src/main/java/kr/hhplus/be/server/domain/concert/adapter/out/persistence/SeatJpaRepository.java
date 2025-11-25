package kr.hhplus.be.server.domain.concert.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeatJpaRepository extends JpaRepository<SeatJpaEntity, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SeatJpaEntity s WHERE s.id = :id")
    Optional<SeatJpaEntity> findByIdForUpdate(@Param("id") String id);
}
