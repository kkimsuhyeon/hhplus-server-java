package kr.hhplus.be.server.domain.payment.model;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.domain.payment.PaymentFixture;
import kr.hhplus.be.server.domain.payment.exception.PaymentErrorCode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    void create_test1() {
        Payment payment = Payment.create("1", BigDecimal.valueOf(1000));

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    void create_test2() {
        assertThatThrownBy(() -> Payment.create(null, BigDecimal.valueOf(1000)))
                .isInstanceOf(BusinessException.class)
                .hasMessage(CommonErrorCode.INVALID_INPUT.getMessage());
    }

    @Test
    void pay_test1() {
        Payment payment = PaymentFixture
                .aPayment()
                .build();

        payment.pay();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    void pay_test2() {
        Payment payment = PaymentFixture
                .aPayment()
                .status(PaymentStatus.CANCEL)
                .build();

        assertThatThrownBy(payment::pay)
                .isInstanceOf(BusinessException.class)
                .hasMessage(PaymentErrorCode.DISABLE_PAY.getMessage());
    }


}