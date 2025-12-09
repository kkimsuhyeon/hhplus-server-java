package kr.hhplus.be.server.domain.token.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class QueueToken {

    private String id;

    private String userId;

    private TokenStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime expiredAt;

    public boolean isActive() {
        return this.status == TokenStatus.ACTIVE;
    }

    public void activate() {
        this.status = TokenStatus.ACTIVE;
        this.expiredAt = LocalDateTime.now().plusMinutes(20);
    }

    public boolean isExpired() {
        return this.expiredAt.isBefore(LocalDateTime.now());
    }

    public boolean isWaiting() {
        return this.status == TokenStatus.WAITING;
    }

    public static QueueToken create(String userId) {
        return QueueToken.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .status(TokenStatus.WAITING)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(60))
                .build();
    }

}
