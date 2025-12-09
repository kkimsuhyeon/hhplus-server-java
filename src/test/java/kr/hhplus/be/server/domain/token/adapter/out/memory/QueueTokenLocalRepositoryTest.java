package kr.hhplus.be.server.domain.token.adapter.out.memory;

import kr.hhplus.be.server.domain.token.model.QueueToken;
import kr.hhplus.be.server.domain.token.model.TokenStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class QueueTokenLocalRepositoryTest {

    @InjectMocks
    private QueueTokenMemoryRepository queueTokenLocalRepository;

    @Test
    void save_Success() {
        QueueToken token = QueueToken.create("userId");
        queueTokenLocalRepository.save("key1", token);

        assertThat(queueTokenLocalRepository.countByStatus(TokenStatus.WAITING)).isEqualTo(1);
    }

    @Test
    void save_Success2() {
        QueueToken token1 = QueueToken.create("userId1");
        QueueToken token2 = QueueToken.create("userId2");
        queueTokenLocalRepository.save("key1", token1);
        queueTokenLocalRepository.save("key1", token2);

        assertThat(queueTokenLocalRepository.countByStatus(TokenStatus.WAITING)).isEqualTo(1);
        assertThat(queueTokenLocalRepository.findById("key1"))
                .isNotNull()
                .hasValueSatisfying(token -> {
                    assertThat(token.getUserId()).isEqualTo("userId2");
                })
        ;
    }

    @Test
    void save_Success3() {
        QueueToken token1 = QueueToken.create("userId1");
        QueueToken token2 = QueueToken.create("userId2");
        queueTokenLocalRepository.save("key1", token1);
        queueTokenLocalRepository.save("key2", token2);

        assertThat(queueTokenLocalRepository.countByStatus(TokenStatus.WAITING)).isEqualTo(2);
    }

    @Test
    void findById_Success() {
        QueueToken token = QueueToken.create("userId");
        queueTokenLocalRepository.save("key1", token);

        assertThat(queueTokenLocalRepository.findById("key1"))
                .isNotNull()
                .hasValueSatisfying(t -> assertThat(t.getUserId()).isEqualTo("userId"));
    }

    @Test
    void findById_Fail() {
        assertThat(queueTokenLocalRepository.findById("key1"))
                .isEqualTo(Optional.empty());
    }

    @Test
    void findByUserId_Success() {
        QueueToken token = QueueToken.builder().id("1").userId("a").build();
        queueTokenLocalRepository.save("key1", token);

        assertThat(queueTokenLocalRepository.findByUserId("a"))
                .isNotNull()
                .hasValueSatisfying(t -> {
                    assertThat(t.getId()).isEqualTo("1");
                    assertThat(t.getUserId()).isEqualTo("a");
                });
    }

    @Test
    void findByUserId_Fail() {
        assertThat(queueTokenLocalRepository.findByUserId("test"))
                .isEqualTo(Optional.empty());
    }

    @Test
    void findByUserId_Fail2() {
        assertThat(queueTokenLocalRepository.findByUserId(null))
                .isEqualTo(Optional.empty());
    }
}