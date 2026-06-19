package kr.hhplus.be.server.domain.payment.model;

import io.micrometer.common.util.StringUtils;
import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.domain.payment.exception.PaymentErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Payment {

    private String id;

    private PaymentStatus status;

    private BigDecimal amount;

    private String reservationId;

    private String rmk;

    public static Payment create(String reservationId, BigDecimal amount) {
        return Payment.of(null, PaymentStatus.PENDING, amount, reservationId, null);
    }

    public static Payment of(String id, PaymentStatus status, BigDecimal amount, String reservationId, String rmk) {
        if (StringUtils.isEmpty(reservationId)) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT);
        }

        return new Payment(id, status, amount, reservationId, rmk);
    }

    public void pay() {
        if (this.status != PaymentStatus.PENDING) {
            throw new BusinessException(PaymentErrorCode.DISABLE_PAY);
        }

        this.status = PaymentStatus.SUCCESS;
    }

    public void changeRmk(String rmk) {
        this.rmk = rmk;
    }

    public boolean isSuccess() {
        return this.status == PaymentStatus.SUCCESS;
    }

}
