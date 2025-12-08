package kr.hhplus.be.server.domain.token.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class QueueTokenTest {

    @Test
    void create_Test() {
        QueueToken actual = QueueToken.create("1234");

        assertThat(actual.getUserId()).isEqualTo("1234");
        assertThat(actual.getStatus()).isEqualTo(TokenStatus.WAITING);
        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void isActive_Test() {
        QueueToken activeToken = QueueToken.builder().status(TokenStatus.ACTIVE).build();
        QueueToken inActiveToken = QueueToken.builder().status(TokenStatus.WAITING).build();

        assertThat(activeToken.isActive()).isTrue();
        assertThat(inActiveToken.isActive()).isFalse();
    }

    @Test
    void activate_Test() {
        QueueToken token = QueueToken.builder().status(TokenStatus.WAITING).build();

        token.activate();

        assertThat(token.getStatus()).isEqualTo(TokenStatus.ACTIVE);
    }

    @Test
    void expire_Test() {
        QueueToken token = QueueToken.builder().expiredAt(LocalDateTime.now().minusMinutes(1)).build();

        assertThat(token.isExpired()).isTrue();
    }

    @Test
    void isExpired_Test() {
        QueueToken expiredToken = QueueToken.builder().expiredAt(LocalDateTime.now().minusMinutes(1)).build();
        QueueToken normalToken = QueueToken.builder().expiredAt(LocalDateTime.now().plusMinutes(1)).build();

        assertThat(expiredToken.isExpired()).isTrue();
        assertThat(normalToken.isExpired()).isFalse();
    }

    @Test
    void isWaiting_Test() {
        QueueToken waitingToken = QueueToken.builder().status(TokenStatus.WAITING).build();
        QueueToken activeToken = QueueToken.builder().status(TokenStatus.ACTIVE).build();

        assertThat(waitingToken.isWaiting()).isTrue();
        assertThat(activeToken.isWaiting()).isFalse();
    }
}