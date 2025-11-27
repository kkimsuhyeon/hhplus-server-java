package kr.hhplus.be.server.domain.payment.adapter.out.persistence;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.payment.application.PaymentRepository;
import kr.hhplus.be.server.domain.payment.exception.PaymentErrorCode;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.reservation.adapter.out.persistence.ReservationEntity;
import kr.hhplus.be.server.domain.reservation.adapter.out.persistence.ReservationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;

    private final ReservationJpaRepository reservationRepository;

    @Override
    public Optional<Payment> findById(String id) {
        return jpaRepository.findById(id)
                .map(PaymentEntity::toModel);
    }

    @Override
    public Payment save(Payment payment) {
        ReservationEntity reservationEntity = reservationRepository.getReferenceById(payment.getReservationId());

        PaymentEntity entity = PaymentEntity.create(payment, reservationEntity);
        return jpaRepository.save(entity).toModel();
    }

    @Override
    public Payment update(Payment payment) {
        PaymentEntity paymentEntity = jpaRepository.findById(payment.getId())
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.NOT_FOUND));

        paymentEntity.update(payment);

        return paymentEntity.toModel();
    }
}
