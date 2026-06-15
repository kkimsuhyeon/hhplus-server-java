package kr.hhplus.be.server.domain.user.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.domain.user.model.UserRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
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

    @Column(name = "email", unique = true)
    @Comment("이메일")
    private String email;

    @Column(name = "password", nullable = false)
    @Comment("패스워드")
    private String password;

    @Column(name = "name", nullable = true)
    @Comment("이름")
    private String name;

    @Builder.Default
    @Column(name = "balance", nullable = false)
    @Comment("잔액")
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Comment("권한")
    private UserRole role;

    @Version
    private Long version;

    public static UserEntity create(User user) {
        return UserEntity.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .balance(user.getBalance())
                .role(user.getRole())
                .build();
    }

    public User toModel() {
        return User.of(this.id, this.email, this.password, this.name, this.balance, this.role);
    }

    public void update(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.name = user.getName();
        this.balance = user.getBalance();
        this.role = user.getRole();
    }

}
