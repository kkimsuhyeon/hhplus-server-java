package kr.hhplus.be.server.domain.payment.adapter.persistence;

import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;
import kr.hhplus.be.server.domain.payment.model.entity.PaymentEntity;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

/**
 * JPA Entity와 도메인 Model 간의 변환을 담당
 */
@Component
public class PaymentMapper {

    /**
     * JPA Entity → 도메인 Model 변환
     */
    public Payment toDomain(PaymentEntity entity) {
        return Payment.builder()
                .id(entity.getId())
                .status(convertStatus(entity.getStatus()))
                .amount(BigInteger.valueOf(entity.getAmount()))
                .reservationId(entity.getReservation().getId())
                .build();
    }

    /**
     * 도메인 Model의 변경사항을 JPA Entity에 반영
     */
    public void updateEntity(PaymentEntity entity, Payment domain) {
        entity.updateFromDomain(convertToEntityStatus(domain.getStatus()));
    }

    /**
     * 기존 Entity의 PaymentStatus → 새 도메인 Model의 PaymentStatus 변환
     */
    private PaymentStatus convertStatus(kr.hhplus.be.server.domain.payment.model.entity.PaymentStatus entityStatus) {
        return switch (entityStatus) {
            case SUCCESS -> PaymentStatus.SUCCESS;
            case FAIL -> PaymentStatus.FAIL;
            case CANCEL -> PaymentStatus.CANCEL;
        };
    }

    /**
     * 새 도메인 Model의 PaymentStatus → 기존 Entity의 PaymentStatus 변환
     */
    private kr.hhplus.be.server.domain.payment.model.entity.PaymentStatus convertToEntityStatus(PaymentStatus domainStatus) {
        return switch (domainStatus) {
            case SUCCESS -> kr.hhplus.be.server.domain.payment.model.entity.PaymentStatus.SUCCESS;
            case FAIL -> kr.hhplus.be.server.domain.payment.model.entity.PaymentStatus.FAIL;
            case CANCEL -> kr.hhplus.be.server.domain.payment.model.entity.PaymentStatus.CANCEL;
        };
    }
}
