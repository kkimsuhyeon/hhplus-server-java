package kr.hhplus.be.server.domain.user.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.user.application.command.CreateUserCommand;
import kr.hhplus.be.server.domain.user.application.mapper.UserMapper;
import kr.hhplus.be.server.domain.user.model.entity.UserEntity;
import kr.hhplus.be.server.domain.user.model.exception.UserErrorCode;
import kr.hhplus.be.server.domain.user.model.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Test
    @DisplayName("유저 조회 - 성공")
    void getUser_Success() {
        UserEntity expectedUser = UserEntity.builder().id("1").balance(BigInteger.valueOf(10000)).build();

        given(userRepository.findById("1")).willReturn(Optional.of(expectedUser));

        UserEntity actualUser = userService.getUser("1");

        assertThat(actualUser).isNotNull();
        assertThat(actualUser.getId()).isEqualTo("1");
        assertThat(actualUser.getBalance()).isEqualTo(BigInteger.valueOf(10000));
        assertThat(actualUser)
                .extracting("id", "balance")
                .containsExactly("1", BigInteger.valueOf(10000));
        assertThat(actualUser).isEqualTo(expectedUser);
    }

    @Test
    @DisplayName("유저 조회 - 실패")
    void getUser_Fail() {
        given(userRepository.findById("1")).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser("1"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(UserErrorCode.NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("유저 생성")
    void createUser() {
        CreateUserCommand command = CreateUserCommand.builder().build();
        UserEntity userMock = mock(UserEntity.class);

        when(userMapper.toEntity(command)).thenReturn(userMock);

        userService.create(command);

        verify(userRepository, times(1)).save(userMock);
    }

}