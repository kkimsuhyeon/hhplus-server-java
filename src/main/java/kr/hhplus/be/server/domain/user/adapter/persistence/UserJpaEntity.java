package kr.hhplus.be.server.domain.user.adapter.persistence;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigInteger;

/**
 * JPA Entity - User
 * 순수하게 데이터베이스 매핑만 담당하며 비즈니스 로직은 없음
 */
@Getter
@Builder(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private String id;

    @Builder.Default
    @Column(name = "balance", nullable = false)
    @Comment("잔액")
    private BigInteger balance = BigInteger.ZERO;
    
    /**
     * 도메인 모델의 변경사항을 Entity에 반영
     * Adapter에서 호출됨
     */
    void updateFromDomain(BigInteger balance) {
        this.balance = balance;
    }
}
