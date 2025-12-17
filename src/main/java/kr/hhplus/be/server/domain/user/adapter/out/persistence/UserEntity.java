package kr.hhplus.be.server.domain.user.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.Version;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Getter
@Builder(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
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
    private BigDecimal balance = BigDecimal.ZERO;

    @Version
    private Long version;

    public static UserEntity create(User user) {
        return UserEntity.builder()
                .balance(user.getBalance())
                .build();
    }

    public User toModel() {
        return User.builder()
                .id(this.id)
                .balance(this.balance)
                .build();
    }

    public void update(User user) {
        this.balance = user.getBalance();
    }

}
