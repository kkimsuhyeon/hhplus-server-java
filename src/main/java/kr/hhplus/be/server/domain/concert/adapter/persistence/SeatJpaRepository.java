package kr.hhplus.be.server.domain.concert.adapter.persistence;

import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatJpaRepository extends JpaRepository<SeatEntity, String>, JpaSpecificationExecutor<SeatEntity> {

    /**
     * ID로 좌석 조회 (비관적 락)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SeatEntity s WHERE s.id = :id")
    Optional<SeatEntity> findByIdForUpdate(@Param("id") String id);

    /**
     * 스케줄 ID로 좌석 목록 조회
     */
    @Query("SELECT s FROM SeatEntity s WHERE s.schedule.id = :scheduleId")
    List<SeatEntity> findByScheduleId(@Param("scheduleId") String scheduleId);
}
