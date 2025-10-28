package kr.hhplus.be.server.domain.user.application.service;

import kr.hhplus.be.server.domain.user.model.entity.UserEntity;
import kr.hhplus.be.server.domain.user.model.repository.UserRepository;
import kr.hhplus.be.server.domain.user.model.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void getUser_Success() {
        UserEntity user = UserEntity.builder().id("1").balance(BigInteger.valueOf(10)).build();

        given(userRepository.findById("1")).willReturn(user);

        UserEntity actual = userService.getUser("1");

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(user.get().getId());
    }

}