package kr.hhplus.be.server.domain.requestkey.adapter.out.persistence;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.requestkey.model.RequestKey;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Builder(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "request_keys",
        uniqueConstraints = {@UniqueConstraint(name = "uk_request_key_user_key", columnNames = {"user_id", "idempotency_key"})}
)
public class RequestKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "request_key_id")
    @Comment("요청 아이디")
    private String id;

    @Column(name = "user_id", nullable = false)
    @Comment("유저 아이디")
    private String userId;

    @Column(name = "idempotency_key", nullable = false, length = 36)
    @Comment("멱등 키")
    private String idempotencyKey;

    @Column(name = "operation", nullable = false, length = 32)
    @Comment("요청 종류")
    private String operation;

    @Column(name = "payload_hash", nullable = false, length = 64)
    @Comment("해시값")
    private String payloadHash;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("등록 일시")
    private Instant createdAt;

    public RequestKey toModel() {
        return RequestKey.of(this.operation, this.payloadHash);
    }

    public static RequestKeyEntity create(String userId, UUID idempotencyKey, RequestKey requestKey) {
        return RequestKeyEntity.builder()
                .userId(userId)
                .idempotencyKey(idempotencyKey.toString())
                .operation(requestKey.getOperation())
                .payloadHash(requestKey.getPayloadHash())
                .build();
    }
}
