package kr.hhplus.be.server.domain.reservation.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, String>, JpaSpecificationExecutor<ReservationEntity> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM ReservationEntity r WHERE r.id = :id")
    Optional<ReservationEntity> findByIdWithLock(String id);

    Optional<ReservationEntity> findByUserIdAndSeatId(String userId, String seatId);

    List<ReservationEntity> findByUserId(String userId);

    @Query("SELECT r FROM ReservationEntity r WHERE r.status = 'PENDING_PAYMENT' AND r.expiresAt < :now")
    List<ReservationEntity> findExpiredPendingReservations(@Param("now") LocalDateTime now);
}
