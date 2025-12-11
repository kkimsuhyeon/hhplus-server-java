package kr.hhplus.be.server.domain.payment.adapter.out.persistence;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.payment.application.PaymentRepository;
import kr.hhplus.be.server.domain.payment.exception.PaymentErrorCode;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.reservation.adapter.out.persistence.ReservationEntity;
import kr.hhplus.be.server.domain.reservation.adapter.out.persistence.ReservationJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;

    @Override
    public Optional<Payment> findById(String id) {
        return jpaRepository.findById(id)
                .map(PaymentEntity::toModel);
    }

    @Override
    public Optional<Payment> findByIdForUpdate(String id) {
        return jpaRepository.findByIdForUpdate(id)
                .map(PaymentEntity::toModel);
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = PaymentEntity.create(payment);
        return jpaRepository.save(entity).toModel();
    }

    @Override
    public Payment update(Payment payment) {
        PaymentEntity paymentEntity = jpaRepository.getReferenceById(payment.getId());
        
        try {
            paymentEntity.update(payment);
            return paymentEntity.toModel();
        } catch (EntityNotFoundException e) {
            log.error("결제 업데이트 실패: paymentId={}", payment.getId(), e);
            throw new BusinessException(PaymentErrorCode.NOT_FOUND);
        }

    }
}
