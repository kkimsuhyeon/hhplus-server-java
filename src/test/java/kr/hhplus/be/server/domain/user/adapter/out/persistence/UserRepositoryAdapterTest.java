package kr.hhplus.be.server.domain.user.adapter.out.persistence;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.user.UserFixture;
import kr.hhplus.be.server.domain.user.port.UserCriteria;
import kr.hhplus.be.server.domain.user.port.UserRepository;
import kr.hhplus.be.server.domain.user.exception.UserErrorCode;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.domain.user.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserRepositoryAdapter.class)
class UserRepositoryAdapterTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager em;

    @Nested
    @DisplayName("save")
    class SaveTest {

        @Test
        @DisplayName("저장하면 id가 생성되고 DB에 반영된다")
        void save() {
            User user = UserFixture.aUser().balance(BigDecimal.valueOf(1000)).build();
            User savedUser = userRepository.save(user);
            em.flush();
            em.clear();

            Optional<User> actual = userRepository.findById(savedUser.getId());

            assertThat(actual).isPresent().get().satisfies(u -> {
                assertThat(u.getId()).isEqualTo(savedUser.getId());
                assertThat(u.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
            });
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTest {

        @Test
        @DisplayName("저장된 유저의 잔액을 변경하면 DB에 반영된다")
        void update() {
            User user = persistedUser("test@test.com");
            assertThat(user.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);

            User updatedUser = User.of(user.getId(), user.getEmail(), user.getPassword(), BigDecimal.valueOf(1000), UserRole.USER);

            userRepository.update(updatedUser);
            em.flush();
            em.clear();

            Optional<User> actual = userRepository.findById(user.getId());

            assertThat(actual).isPresent().get().satisfies(u -> {
                assertThat(u.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
            });
        }

        @Test
        void update_fail() {
            User ghost = User.of("no_such_id", "test@test.com", "password", BigDecimal.ZERO, UserRole.USER);
            assertThatThrownBy(() -> userRepository.update(ghost))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode").isEqualTo(UserErrorCode.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("findByEmail")
    class FindByEmailTest {

        @BeforeEach
        void setup() {
            persistedUser("a@test.com");
            persistedUser("b@test.com");

            em.clear();
        }

        @Test
        @DisplayName("저장된 이메일로 조회하면 유저가 매핑되어 돌아온다")
        void findByEmail_found() {
            final String EMAIL = "a@test.com";

            // when
            Optional<User> found = userRepository.findByEmail(EMAIL);

            // then
            assertThat(found).isPresent()
                    .get().satisfies(user -> {
                        assertThat(user.getEmail()).isEqualTo(EMAIL);
                        assertThat(user.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
                    });
        }

        @Test
        @DisplayName("없는 이메일로 조회하면 Optional.empty를 반환한다")
        void findByEmail_notFound() {
            // when
            Optional<User> found = userRepository.findByEmail("none@test.com");

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("저장된 id로 조회하면 해당 유저가 매핑되어 돌아온다")
        void findById_found() {
            User user = persistedUser("test@test.com");
            em.clear();

            Optional<User> actual = userRepository.findById(user.getId());

            assertThat(actual).isPresent().get().satisfies(result -> {
                assertThat(result.getId()).isEqualTo(user.getId());
                assertThat(result.getEmail()).isEqualTo("test@test.com");
            });
        }

        @Test
        @DisplayName("없는 id로 조회하면 Optional.empty를 반환한다")
        void findById_notFound() {
            Optional<User> actual = userRepository.findById("no_such_id");

            assertThat(actual).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByEmail")
    class ExistsByEmailTest {

        @BeforeEach
        void setup() {
            persistedUser("a@test.com");
            persistedUser("b@test.com");

            em.clear();
        }

        @Test
        @DisplayName("저장된 이메일이면 true를 반환한다")
        void existsByEmail_exists() {
            assertThat(userRepository.existsByEmail("a@test.com")).isTrue();
        }

        @Test
        @DisplayName("없는 이메일이면 false를 반환한다")
        void existsByEmail_nonExists() {
            assertThat(userRepository.existsByEmail("none@test.com")).isFalse();
        }
    }

    @Nested
    @DisplayName("findByCriteria")
    class FindByCriteriaTest {

        @BeforeEach
        void setup() {
            persistedUser("a@test.com", UserRole.USER);
            persistedUser("b@test.com", UserRole.ADMIN);

            em.clear();
        }

        @Test
        @DisplayName("이메일 부분일치로 필터하면 매칭되는 유저만 조회된다")
        void findByCriteria_byEmail() {
            UserCriteria criteria = UserCriteria.builder()
                    .email("a@")
                    .build();

            Page<User> actual = userRepository.findAllByCriteria(criteria, Pageable.unpaged());

            assertThat(actual.getContent())
                    .extracting(User::getEmail)
                    .containsExactlyInAnyOrder("a@test.com");
        }

        @Test
        @DisplayName("역할로 필터하면 해당 역할 유저만 조회된다")
        void findByCriteria_byRole() {
            UserCriteria criteria = UserCriteria.builder()
                    .role(UserRole.ADMIN)
                    .build();

            Page<User> actual = userRepository.findAllByCriteria(criteria, Pageable.unpaged());

            assertThat(actual.getContent())
                    .extracting(User::getEmail)
                    .containsExactlyInAnyOrder("b@test.com");
        }

        @Test
        @DisplayName("이메일과 역할을 모두 만족하는 유저만 조회된다")
        void findByCriteria_byEmailAndRole() {
            UserCriteria criteria = UserCriteria.builder()
                    .email("test")
                    .role(UserRole.ADMIN)
                    .build();

            Page<User> actual = userRepository.findAllByCriteria(criteria, Pageable.unpaged());

            assertThat(actual.getContent())
                    .extracting(User::getEmail, User::getRole)
                    .containsExactlyInAnyOrder(
                            tuple("b@test.com", UserRole.ADMIN)
                    );
        }

        @Test
        @DisplayName("조건이 없으면 전체 조회된다")
        void findByCriteria_all() {
            UserCriteria criteria = UserCriteria.builder().build();

            Page<User> actual = userRepository.findAllByCriteria(criteria, Pageable.unpaged());

            assertThat(actual.getContent())
                    .hasSize(2);
        }
    }

    private User persistedUser(String email) {
        UserEntity entity = em.persistAndFlush(UserEntity.create(UserFixture.aUser()
                .email(email)
                .build()));

        return entity.toModel();
    }

    private User persistedUser(String email, UserRole role) {
        UserEntity entity = em.persistAndFlush(UserEntity.create(UserFixture.aUser()
                .email(email)
                .role(role)
                .build()));

        return entity.toModel();
    }


}