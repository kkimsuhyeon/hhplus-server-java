package kr.hhplus.be.server.domain.user.model;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.domain.user.exception.UserErrorCode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    private static final String EMAIL = "test@test.com";
    private static final String PASSWORD = "password123";

    @Test
    void create_User_Success() {
        User user = getTestUser();

        assertThat(user.getBalance()).isEqualTo(BigDecimal.ZERO);
        assertThat(user.getEmail()).isEqualTo(EMAIL);
        assertThat(user.getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    void addBalance_Success() {
        User user = getTestUser();

        user.addBalance(BigDecimal.valueOf(100));

        assertThat(user.getBalance()).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    void addBalance_Fail() {
        User user = User.builder().balance(BigDecimal.ZERO).build();

        assertThatThrownBy(() -> user.addBalance(BigDecimal.valueOf(-100)))
                .isInstanceOf(BusinessException.class)
                .hasMessage(CommonErrorCode.INVALID_INPUT.getMessage());
    }

    @Test
    void deductBalance_Success() {
        User user = User.builder().balance(BigDecimal.valueOf(1000)).build();

        user.deductBalance(BigDecimal.valueOf(500));

        assertThat(user.getBalance()).isEqualTo(BigDecimal.valueOf(500));
    }

    @Test
    void deductBalance_Fail() {
        User user = User.builder().balance(BigDecimal.valueOf(1000)).build();

        assertThatThrownBy(() -> user.deductBalance(BigDecimal.valueOf(1500)))
                .isInstanceOf(BusinessException.class)
                .hasMessage(UserErrorCode.NOT_ENOUGH_POINT.getMessage());
    }

    private User getTestUser() {
        return User.createUser(EMAIL, PASSWORD);
    }

}