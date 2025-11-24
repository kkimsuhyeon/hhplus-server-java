package kr.hhplus.be.server.domain.payment.application;

import kr.hhplus.be.server.domain.payment.model.Payment;

import java.util.Optional;

/**
 * Payment Repository 인터페이스
 * 순수 도메인 모델(Payment)만 다루며, JPA에 대해 전혀 알지 못함
 */
public interface PaymentRepository {

    /**
     * ID로 결제 조회
     */
    Payment findById(String id);

    /**
     * 예약 ID로 결제 조회
     */
    Optional<Payment> findByReservationId(String reservationId);

    /**
     * 결제 저장 (생성 및 수정)
     */
    Payment save(Payment payment);
}
