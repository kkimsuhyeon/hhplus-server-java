package kr.hhplus.be.server.domain.payment.adapter.persistence;

import kr.hhplus.be.server.domain.payment.model.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, String>, JpaSpecificationExecutor<PaymentEntity> {

    /**
     * 예약 ID로 결제 조회
     */
    @Query("SELECT p FROM PaymentEntity p WHERE p.reservation.id = :reservationId")
    Optional<PaymentEntity> findByReservationId(@Param("reservationId") String reservationId);
}
