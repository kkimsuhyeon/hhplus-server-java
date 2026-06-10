package kr.hhplus.be.server.domain.user.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.user.exception.UserErrorCode;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.domain.user.model.UserRole;
import kr.hhplus.be.server.domain.user.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRegistration 테스트")
class UserRegistrationTest {

    @Mock
    private UserRepository userRepository;

    private UserRegistration userRegistration;

    @BeforeEach
    void setUp() {
        userRegistration = new UserRegistration(userRepository, raw -> "hashed:" + raw);
    }

    @Test
    @DisplayName("중복되지 않은 이메일이면 비밀번호를 해시해서 유저를 생성한다")
    void register_success() {
        // given
        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);

        // when
        User user = userRegistration.register("test@test.com", "password123");

        // then
        assertThat(user.getEmail()).isEqualTo("test@test.com");
        assertThat(user.getPassword()).isEqualTo("hashed:password123");
        assertThat(user.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(user.getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("이미 존재하는 이메일이면 DUPLICATE_EMAIL 예외가 발생한다")
    void register_fail_duplicateEmail() {
        // given
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        // when, then
        assertThatThrownBy(() -> userRegistration.register("test@test.com", "password123"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(UserErrorCode.DUPLICATE_EMAIL.getMessage());
    }
}
