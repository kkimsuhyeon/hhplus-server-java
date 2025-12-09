package kr.hhplus.be.server.domain.payment.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Payment {

    private String id;

    private PaymentStatus status;

    private BigDecimal amount;

    private String reservationId;

    public static Payment createSuccess(String reservationId, BigDecimal amount) {
        return Payment.builder()
                .reservationId(reservationId)
                .status(PaymentStatus.SUCCESS)
                .amount(amount)
                .build();
    }

    public static Payment createFail(String reservationId, BigDecimal amount) {
        return Payment.builder()
                .reservationId(reservationId)
                .status(PaymentStatus.FAIL)
                .amount(amount)
                .build();
    }

    public void cancel() {
        this.status = PaymentStatus.CANCEL;
    }

    public boolean isSuccess() {
        return this.status == PaymentStatus.SUCCESS;
    }

}
