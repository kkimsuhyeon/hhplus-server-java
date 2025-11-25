package kr.hhplus.be.server.domain.payment.adapter.out.persistence;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.payment.application.PaymentRepository;
import kr.hhplus.be.server.domain.payment.exception.PaymentErrorCode;
import kr.hhplus.be.server.domain.payment.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;

    @Override
    public Payment findById(String id) {
        return jpaRepository.findById(id)
                .map(PaymentJpaEntity::toModel)
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.NOT_FOUND));
    }

    @Override
    public Optional<Payment> findByReservationId(String reservationId) {
        return jpaRepository.findByReservationId(reservationId)
                .map(PaymentJpaEntity::toModel);
    }

    @Override
    public Payment save(Payment payment) {
        if (payment.getId() != null) {
            PaymentJpaEntity entity = jpaRepository.findById(payment.getId())
                    .orElseThrow(() -> new BusinessException(PaymentErrorCode.NOT_FOUND));

            entity.update(payment);

            return entity.toModel();
        } else {
            PaymentJpaEntity entity = PaymentJpaEntity.create(payment);
            PaymentJpaEntity savedEntity = jpaRepository.save(entity);

            return savedEntity.toModel();
        }
    }
}
