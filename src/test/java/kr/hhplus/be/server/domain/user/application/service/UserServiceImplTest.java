package kr.hhplus.be.server.domain.user.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.user.application.UserRepository;
import kr.hhplus.be.server.domain.user.application.UserService;
import kr.hhplus.be.server.domain.user.application.dto.command.CreateUserCommand;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
        assertThrows(BusinessException.class, () -> {
            userService.getUser("123");
        });
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
        assertThat(actualUser.getId()).isNotNull();
        assertThat(actualUser.getBalance()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("포인트 추가")
    void createUser() {
        CreateUserCommand command = CreateUserCommand.builder().build();
        userService.create(command);

        verify(repository, times(1)).save(any());

    }

}