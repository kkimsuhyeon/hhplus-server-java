package kr.hhplus.be.server.domain.user.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.user.application.repository.UserRepository;
import kr.hhplus.be.server.domain.user.exception.UserErrorCode;
import kr.hhplus.be.server.domain.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserQueryService 테스트")
class UserQueryServiceTest {

    @InjectMocks
    private UserQueryService userQueryService;

    @Mock
    private UserRepository repository;

    @Test
    @DisplayName("유저 조회 - 성공")
    void getUser_Success() {
        // given
        User expectedUser = User.of("123", null, null, null, null);
        when(repository.findById("123")).thenReturn(Optional.of(expectedUser));

        // when
        User actualUser = userQueryService.getUser("123");

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
        assertThatThrownBy(() -> userQueryService.getUser("123"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(UserErrorCode.NOT_FOUND.getMessage());
    }
}
