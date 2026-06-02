package kr.hhplus.be.server.domain.user.model;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.domain.user.UserFixture;
import kr.hhplus.be.server.domain.user.exception.UserErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    private static final String EMAIL = "test@test.com";
    private static final String PASSWORD = "password123";

    @Nested
    @DisplayName("createUser")
    class CreateUserTest {
        @Test
        @DisplayName("유저 생성 시 잔액 0, 권한 USER로 초기화된다")
        void createUser_success() {
            User user = User.create(EMAIL, PASSWORD);

            assertThat(user.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(user.getEmail()).isEqualTo(EMAIL);
            assertThat(user.getPassword()).isEqualTo(PASSWORD);
            assertThat(user.getRole()).isEqualTo(UserRole.USER);
        }
    }

    @Nested
    @DisplayName("addBalance")
    class AddBalanceTest {
        @Test
        @DisplayName("정상 금액 충전 시 잔액이 증가한다")
        void addBalance_success() {
            User user = UserFixture.withBalance(BigDecimal.ZERO);

            user.addBalance(BigDecimal.valueOf(100));

            assertThat(user.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(100));
        }

        @Test
        @DisplayName("음수 금액 충전 시 INVALID_INPUT 예외가 발생한다")
        void addBalance_fail1() {
            User user = UserFixture.withBalance(BigDecimal.ZERO);

            assertThatThrownBy(() -> user.addBalance(BigDecimal.valueOf(-100)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(CommonErrorCode.INVALID_INPUT.getMessage());
        }

        @Test
        @DisplayName("0원 충전 시 INVALID_INPUT 예외가 발생한다")
        void addBalance_fail2() {
            User user = UserFixture.withBalance(BigDecimal.ZERO);

            assertThatThrownBy(() -> user.addBalance(BigDecimal.valueOf(0)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(CommonErrorCode.INVALID_INPUT.getMessage());
        }

        @Test
        @DisplayName("null 금액 충전 시 INVALID_INPUT 예외가 발생한다")
        void addBalance_fail3() {
            User user = UserFixture.withBalance(BigDecimal.ZERO);

            assertThatThrownBy(() -> user.addBalance(null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(CommonErrorCode.INVALID_INPUT.getMessage());
        }
    }

    @Nested
    @DisplayName("deductBalance")
    class DeductBalanceTest {
        @Test
        @DisplayName("정상 금액 차감 시 잔액이 감소한다")
        void deductBalance_success1() {
            User user = UserFixture.withBalance(BigDecimal.valueOf(1000));

            user.deductBalance(BigDecimal.valueOf(500));

            assertThat(user.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(500));
        }

        @Test
        @DisplayName("잔액 전액 차감 시 잔액이 0이 된다")
        void deductBalance_success2() {
            User user = UserFixture.withBalance(BigDecimal.valueOf(1000));

            user.deductBalance(BigDecimal.valueOf(1000));

            assertThat(user.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(0));
        }

        @Test
        @DisplayName("음수 금액 차감 시 INVALID_INPUT 예외가 발생한다")
        void deductBalance_fail1() {
            User user = UserFixture.withBalance(BigDecimal.valueOf(1000));

            assertThatThrownBy(() -> user.deductBalance(BigDecimal.valueOf(-100)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(CommonErrorCode.INVALID_INPUT.getMessage());
        }

        @Test
        @DisplayName("잔액보다 큰 금액 차감 시 NOT_ENOUGH_POINT 예외가 발생한다")
        void deductBalance_fail2() {
            User user = UserFixture.withBalance(BigDecimal.valueOf(1000));

            assertThatThrownBy(() -> user.deductBalance(BigDecimal.valueOf(1001)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(UserErrorCode.NOT_ENOUGH_POINT.getMessage());
        }

        @Test
        @DisplayName("null 금액 차감 시 INVALID_INPUT 예외가 발생한다")
        void deductBalance_fail3() {
            User user = UserFixture.withBalance(BigDecimal.valueOf(1000));

            assertThatThrownBy(() -> user.deductBalance(null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(CommonErrorCode.INVALID_INPUT.getMessage());
        }
    }

}