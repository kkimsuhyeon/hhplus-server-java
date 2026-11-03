package kr.hhplus.be.server.domain.requestkey.adapter.out.persistence;

import kr.hhplus.be.server.config.jpa.JpaConfig;
import kr.hhplus.be.server.domain.requestkey.model.RequestKey;
import kr.hhplus.be.server.domain.requestkey.port.RequestKeyRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({RequestKeyRepositoryAdapter.class, JpaConfig.class})
class RequestKeyRepositoryAdapterTest {

    @Autowired
    private RequestKeyRepository requestKeyRepository;

    @Autowired
    private TestEntityManager em;

    private static final UUID IDEMPOTENCY_KEY = UUID.randomUUID();
    private static final String USER_ID = "user";

    @Test
    void 저장한_요청을_조회할_수_있다() {
        RequestKey requestKey = RequestKey.of("CHARGE", "abc");
        requestKeyRepository.save(USER_ID, IDEMPOTENCY_KEY, requestKey);

        em.flush();
        em.clear();

        Optional<RequestKey> actual = requestKeyRepository.findRequest(USER_ID, IDEMPOTENCY_KEY);

        assertThat(actual).isPresent().get().satisfies(key -> {
            assertThat(key.getOperation()).isEqualTo(requestKey.getOperation());
            assertThat(key.getPayloadHash()).isEqualTo("abc");
        });
    }

    @Test
    void 저장하지_않은_키는_빈값이다() {
        Optional<RequestKey> actual = requestKeyRepository.findRequest(USER_ID, IDEMPOTENCY_KEY);

        assertThat(actual).isEmpty();
    }

    @Test
    void 같은_유저가_같은_키로_두번_저장하면_예외() {
        requestKeyRepository.save(USER_ID, IDEMPOTENCY_KEY, RequestKey.of("CHARGE", "abc"));
        em.flush();
        em.clear();

        requestKeyRepository.save(USER_ID, IDEMPOTENCY_KEY, RequestKey.of("CHARGE", "abc"));

        assertThatThrownBy(() -> em.flush())
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void 다른_유저는_같은_키를_쓸_수_있고_자기_것만_조회된다() {
        String userA = "userA";
        String userB = "userB";

        requestKeyRepository.save(userA, IDEMPOTENCY_KEY, RequestKey.of("CHARGE", "abc"));
        requestKeyRepository.save(userB, IDEMPOTENCY_KEY, RequestKey.of("CHARGE", "efg"));

        em.flush();
        em.clear();

        Optional<RequestKey> actual = requestKeyRepository.findRequest(userB, IDEMPOTENCY_KEY);

        assertThat(actual).isPresent().get().satisfies(key -> {
            assertThat(key.isSameRequest(RequestKey.of("CHARGE", "efg"))).isTrue();
            assertThat(key.getPayloadHash()).isEqualTo("efg");
        });
    }

}