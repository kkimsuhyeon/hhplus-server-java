package kr.hhplus.be.server.domain.concert.adapter.out.persistence.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatJpaRepository extends JpaRepository<SeatEntity, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SeatEntity s WHERE s.id = :id")
    Optional<SeatEntity> findByIdForUpdate(@Param("id") String id);

    List<SeatEntity> findBySchedule_Id(String scheduleId);
}
