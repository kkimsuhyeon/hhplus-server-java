package kr.hhplus.be.server.domain.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;

/**
 * 순수 도메인 모델 - Payment
 */
@Getter
@Builder
@AllArgsConstructor
public class Payment {

    private String id;
    private PaymentStatus status;
    private BigInteger amount;
    private String reservationId;

    /**
     * 결제 성공 처리
     */
    public static Payment createSuccess(String reservationId, BigInteger amount) {
        return Payment.builder()
                .reservationId(reservationId)
                .amount(amount)
                .status(PaymentStatus.SUCCESS)
                .build();
    }

    /**
     * 결제 실패 처리
     */
    public void fail() {
        this.status = PaymentStatus.FAIL;
    }

    /**
     * 결제 취소 처리
     */
    public void cancel() {
        this.status = PaymentStatus.CANCEL;
    }

    /**
     * 결제 성공 여부
     */
    public boolean isSuccess() {
        return this.status == PaymentStatus.SUCCESS;
    }
}
