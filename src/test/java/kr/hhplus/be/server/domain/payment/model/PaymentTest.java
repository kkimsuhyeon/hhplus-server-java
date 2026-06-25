package kr.hhplus.be.server.domain.payment.model;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.domain.payment.PaymentFixture;
import kr.hhplus.be.server.domain.payment.exception.PaymentErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Payment 모델 테스트")
class PaymentTest {

    @Nested
    @DisplayName("결제 생성")
    class CreateTest {

        @Test
        @DisplayName("PENDING 상태로 생성된다")
        void create_success() {
            Payment payment = Payment.create("1", BigDecimal.valueOf(1000));

            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(payment.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        }

        @Test
        @DisplayName("reservationId가 비어있으면 INVALID_INPUT 예외가 발생한다")
        void create_fail_blankReservationId() {
            assertThatThrownBy(() -> Payment.create(null, BigDecimal.valueOf(1000)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(CommonErrorCode.INVALID_INPUT.getMessage());
        }
    }

    @Nested
    @DisplayName("결제(pay)")
    class PayTest {

        @Test
        @DisplayName("PENDING이면 SUCCESS로 전이된다")
        void pay_success() {
            Payment payment = PaymentFixture.aPayment().build();

            payment.pay();

            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        }

        @ParameterizedTest
        @EnumSource(value = PaymentStatus.class, names = "PENDING", mode = EnumSource.Mode.EXCLUDE)
        @DisplayName("PENDING이 아니면 DISABLE_PAY 예외가 발생한다")
        void pay_fail_whenNotPending(PaymentStatus status) {
            Payment payment = PaymentFixture
                    .aPayment()
                    .status(status)
                    .build();

            assertThatThrownBy(payment::pay)
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(PaymentErrorCode.DISABLE_PAY.getMessage());
        }
    }
}
