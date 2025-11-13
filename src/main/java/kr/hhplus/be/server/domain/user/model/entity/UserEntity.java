package kr.hhplus.be.server.domain.user.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.domain.user.model.exception.UserErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigInteger;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private String id;

    @Builder.Default
    @Column(name = "balance", nullable = false)
    @Comment("잔액")
    private BigInteger balance = BigInteger.ZERO;

    public void addBalance(BigInteger amount) {
        if (amount.compareTo(BigInteger.ZERO) < 0) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT);
        }

        this.balance = this.balance.add(amount);
    }

    public void deductBalance(BigInteger amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new BusinessException(UserErrorCode.NOT_ENOUGH_POINT);
        }

        this.balance = this.balance.subtract(amount);
    }
}
