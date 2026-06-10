package kr.hhplus.be.server.domain.user.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.user.UserFixture;
import kr.hhplus.be.server.domain.user.application.dto.command.CreateUserCommand;
import kr.hhplus.be.server.domain.user.port.UserRepository;
import kr.hhplus.be.server.domain.user.service.UserRegistration;
import kr.hhplus.be.server.domain.user.exception.UserErrorCode;
import kr.hhplus.be.server.domain.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserCommandService 테스트")
class UserCommandServiceTest {

    @InjectMocks
    private UserCommandService userCommandService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRegistration userRegistration;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Nested
    @DisplayName("유저 생성")
    class CreateUserTest {
        @Test
        @DisplayName("register가 생성한 유저를 그대로 저장한다")
        void createUser_success() {

            // given
            CreateUserCommand command = CreateUserCommand.builder()
                    .email("test@test.com")
                    .password("password123")
                    .build();

            User user = UserFixture.aUser()
                    .email("test@test.com")
                    .password("hash")
                    .build();

            when(userRegistration.register("test@test.com", "password123"))
                    .thenReturn(user);

            // when
            userCommandService.create(command);

            // then
            verify(userRepository, times(1)).save(userCaptor.capture());

            User actualUser = userCaptor.getValue();
            assertThat(actualUser).isSameAs(user);
        }

    }

    @Nested
    @DisplayName("잔액 충전")
    class AddBalanceTest {

        @Test
        @DisplayName("유저를 조회해 잔액을 더하고 저장한다")
        void addBalance_success() {
            when(userRepository.findByIdForUpdate("test@test.com"))
                    .thenReturn(Optional.of(UserFixture.aUser()
                            .email("test@test.com")
                            .build()));

            userCommandService.addBalance("test@test.com", BigDecimal.valueOf(1000));

            verify(userRepository, times(1)).update(userCaptor.capture());

            User actualUser = userCaptor.getValue();
            assertThat(actualUser.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        }

        @Test
        @DisplayName("유저가 없으면 NOT_FOUND 예외가 발생한다")
        void addBalance_notFound() {
            when(userRepository.findByIdForUpdate("no-user"))
                    .thenReturn(Optional.empty());

            verify(userRepository, never()).update(any());

            assertThatThrownBy(() -> userCommandService.addBalance("no-user", BigDecimal.valueOf(1000)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(UserErrorCode.NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("잔액 차감")
    class DeductBalanceTest {

        @Test
        @DisplayName("유저를 조회해 잔액을 차감하고 저장한다")
        void deductBalance_success() {

            when(userRepository.findByIdForUpdate("test@test.com"))
                    .thenReturn(Optional.of(UserFixture.aUser()
                            .email("test@test.com")
                            .balance(BigDecimal.valueOf(2000))
                            .build()));

            userCommandService.deductBalance("test@test.com", BigDecimal.valueOf(500));

            verify(userRepository, times(1)).update(userCaptor.capture());

            User actualUser = userCaptor.getValue();
            assertThat(actualUser.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1500));

        }

        @Test
        @DisplayName("유저가 없으면 NOT_FOUND 예외가 발생한다")
        void deductBalance_notFound() {
            when(userRepository.findByIdForUpdate("no-user"))
                    .thenReturn(Optional.empty());

            verify(userRepository, never()).update(any());

            assertThatThrownBy(() -> userCommandService.deductBalance("no-user", BigDecimal.valueOf(1000)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(UserErrorCode.NOT_FOUND.getMessage());
        }
    }
}
