package kr.hhplus.be.server.domain.user.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.user.UserFixture;
import kr.hhplus.be.server.domain.user.application.dto.query.FindUserQuery;
import kr.hhplus.be.server.domain.user.model.UserRole;
import kr.hhplus.be.server.domain.user.port.UserCriteria;
import kr.hhplus.be.server.domain.user.port.UserRepository;
import kr.hhplus.be.server.domain.user.exception.UserErrorCode;
import kr.hhplus.be.server.domain.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserQueryService 테스트")
class UserQueryServiceTest {

    @InjectMocks
    private UserQueryService userQueryService;

    @Mock
    private UserRepository repository;

    @Nested
    @DisplayName("단건 조회")
    class GetUserTest {

        @Test
        @DisplayName("유저가 있으면 해당 유저를 반환한다")
        void getUser_Success() {
            // given
            User expectedUser = UserFixture.aUser().id("123").build();
            when(repository.findById("123")).thenReturn(Optional.of(expectedUser));

            // when
            User actualUser = userQueryService.getUser("123");

            // then
            assertThat(actualUser.getId()).isEqualTo(expectedUser.getId());
            verify(repository).findById("123");
        }

        @Test
        @DisplayName("유저가 없으면 NOT_FOUND 예외가 발생한다")
        void getUser_Fail() {
            // given
            when(repository.findById("123")).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> userQueryService.getUser("123"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(UserErrorCode.NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("목록 조회")
    class GetUsersTest {

        @Test
        @DisplayName("query를 criteria로 변환해 repo에 위임하고 결과를 반환한다")
        void getUsers_withQuery() {
            User user1 = UserFixture.aUser().email("a@test.com").role(UserRole.USER).build();

            // given
            FindUserQuery query = FindUserQuery.builder().email("a@test.com").role(UserRole.USER).build();
            Pageable pageable = Pageable.unpaged();
            when(repository.findAllByCriteria(any(), any())).thenReturn(new PageImpl<>(List.of(user1)));

            Page<User> actual = userQueryService.getUsers(query, pageable);

            ArgumentCaptor<UserCriteria> captor = ArgumentCaptor.forClass(UserCriteria.class);
            verify(repository).findAllByCriteria(captor.capture(), eq(pageable));
            assertThat(captor.getValue().getEmail()).isEqualTo("a@test.com");
            assertThat(captor.getValue().getRole()).isEqualTo(UserRole.USER);
            assertThat(actual.getContent()).contains(user1);
        }

        @Test
        @DisplayName("query가 null이면 빈 criteria로 조회한다")
        void getUsers_nullQuery() {
            User user1 = UserFixture.aUser().email("a@test.com").role(UserRole.USER).build();

            FindUserQuery query = null;
            Pageable pageable = Pageable.unpaged();
            when(repository.findAllByCriteria(any(), any())).thenReturn(new PageImpl<>(List.of(user1)));

            Page<User> actual = userQueryService.getUsers(query, pageable);

            ArgumentCaptor<UserCriteria> captor = ArgumentCaptor.forClass(UserCriteria.class);
            verify(repository).findAllByCriteria(captor.capture(), eq(pageable));
            assertThat(captor.getValue().getEmail()).isNull();
            assertThat(captor.getValue().getRole()).isNull();
        }
    }
}
