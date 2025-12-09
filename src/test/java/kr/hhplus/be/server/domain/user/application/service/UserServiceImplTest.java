package kr.hhplus.be.server.domain.user.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.domain.user.application.UserRepository;
import kr.hhplus.be.server.domain.user.application.UserService;
import kr.hhplus.be.server.domain.user.application.dto.command.CreateUserCommand;
import kr.hhplus.be.server.domain.user.exception.UserErrorCode;
import kr.hhplus.be.server.domain.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
class UserServiceImplTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository repository;

    @Test
    @DisplayName("유저 조회 - 성공")
    void getUser_Success() {
        // given
        User expectedUser = User.builder().id("123").build();
        when(repository.findById("123")).thenReturn(Optional.of(expectedUser));

        // when
        User actualUser = userService.getUser("123");

        // then
        assertThat(actualUser.getId()).isEqualTo(expectedUser.getId());
        verify(repository).findById("123");
    }

    @Test
    @DisplayName("유저 조회 - 실패")
    void getUser_Fail() {

        // given
        when(repository.findById("123")).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> userService.getUser("123"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(UserErrorCode.NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("유저 생성")
    void create_Success() {
        // given
        CreateUserCommand command = CreateUserCommand.builder().build();

        // when
        userService.create(command);

        // then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repository, times(1)).save(userCaptor.capture());

        User actualUser = userCaptor.getValue();
        assertThat(actualUser.getBalance()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("포인트 추가")
    void addBalance_Success() {

        // given
        String userId = "123";
        BigDecimal amount = BigDecimal.valueOf(1000);
        User expectedUser = User.builder().id(userId).balance(BigDecimal.ZERO).build();

        when(repository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // when
        userService.addBalance(userId, amount);

        // then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repository, times(1)).update(userCaptor.capture());

        User actualUser = userCaptor.getValue();
        assertThat(actualUser.getBalance()).isEqualTo(amount);
    }

    @Test
    @DisplayName("포인트 추가 - 실패")
    void addBalance_Fail() {
        // given
        User expectedUser = User.builder().id("123").balance(BigDecimal.ZERO).build();
        when(repository.findById("123")).thenReturn(Optional.of(expectedUser));

        // when, then
        assertThatThrownBy(() -> userService.addBalance("123", BigDecimal.valueOf(-100)))
                .isInstanceOf(BusinessException.class)
                .hasMessage(CommonErrorCode.INVALID_INPUT.getMessage());

        verify(repository, times(0)).update(expectedUser);
    }

    @Test
    @DisplayName("포인트 차감 - 성공")
    void deductBalance_Success() {
        User expectedUser = User.builder().id("123").balance(BigDecimal.valueOf(1000)).build();
        when(repository.findById("123")).thenReturn(Optional.of(expectedUser));

        userService.deductBalance("123", BigDecimal.valueOf(100));

        assertThat(expectedUser.getBalance()).isEqualTo(BigDecimal.valueOf(900));
        verify(repository, times(1)).update(expectedUser);
    }

    @Test
    @DisplayName("포인트 차감 - 실패")
    void deductBalance_Fail() {
        User expectedUser = User.builder().id("123").balance(BigDecimal.valueOf(100)).build();
        when(repository.findById("123")).thenReturn(Optional.of(expectedUser));

        assertThatThrownBy(() -> userService.deductBalance("123", BigDecimal.valueOf(500)))
                .isInstanceOf(BusinessException.class)
                .hasMessage(UserErrorCode.NOT_ENOUGH_POINT.getMessage());
    }

}