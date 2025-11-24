package kr.hhplus.be.server.domain.payment.adapter.persistence;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.domain.payment.application.PaymentRepository;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.model.entity.PaymentEntity;
import kr.hhplus.be.server.domain.reservation.model.entity.ReservationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import java.util.Optional;

/**
 * PaymentRepository의 JPA 구현체
 * JPA Entity와 순수 도메인 Model 간의 변환을 담당
 */
@Repository
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;
    private final PaymentMapper mapper;
    private final EntityManager entityManager;

    @Override
    public Payment findById(String id) {
        PaymentEntity entity = jpaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));

        return mapper.toDomain(entity);
    }

    @Override
    public Optional<Payment> findByReservationId(String reservationId) {
        return jpaRepository.findByReservationId(reservationId)
                .map(mapper::toDomain);
    }

    @Override
    public Payment save(Payment payment) {
        if (payment.getId() != null) {
            // 수정: 기존 Entity를 조회해서 업데이트
            PaymentEntity entity = jpaRepository.findById(payment.getId())
                    .orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));

            // 도메인 Model의 변경사항을 Entity에 반영
            mapper.updateEntity(entity, payment);

            // 더티 체킹으로 자동 저장됨
        } else {
            // 신규 생성: 기존 Entity 구조 사용
            ReservationEntity reservation = entityManager.getReference(
                    ReservationEntity.class, payment.getReservationId());

            PaymentEntity entity = PaymentEntity.builder()
                    .status(convertToEntityStatus(payment.getStatus()))
                    .amount(payment.getAmount().longValue())
                    .reservation(reservation)
                    .build();

            PaymentEntity saved = jpaRepository.save(entity);
            return Payment.builder()
                    .id(saved.getId())
                    .status(payment.getStatus())
                    .amount(payment.getAmount())
                    .reservationId(payment.getReservationId())
                    .build();
        }

        return payment;
    }

    private kr.hhplus.be.server.domain.payment.model.entity.PaymentStatus convertToEntityStatus(
            kr.hhplus.be.server.domain.payment.model.PaymentStatus domainStatus) {
        return switch (domainStatus) {
            case SUCCESS -> kr.hhplus.be.server.domain.payment.model.entity.PaymentStatus.SUCCESS;
            case FAIL -> kr.hhplus.be.server.domain.payment.model.entity.PaymentStatus.FAIL;
            case CANCEL -> kr.hhplus.be.server.domain.payment.model.entity.PaymentStatus.CANCEL;
        };
    }
}
