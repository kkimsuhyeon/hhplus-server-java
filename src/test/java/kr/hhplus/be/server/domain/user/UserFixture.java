package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.domain.user.model.UserRole;

import java.math.BigDecimal;

public class UserFixture {

    private String email = "test@test.com";
    private String password = "password123";
    private BigDecimal balance = BigDecimal.ZERO;
    private UserRole role = UserRole.USER;

    public static UserFixture aUser() {
        return new UserFixture();
    }

    public UserFixture email(String v) {
        this.email = v;
        return this;
    }

    public UserFixture password(String v) {
        this.password = v;
        return this;
    }

    public UserFixture role(UserRole v) {
        this.role = v;
        return this;
    }

    public UserFixture balance(BigDecimal v) {
        this.balance = v;
        return this;
    }

    public User build() {
        return User.of(null, email, password, balance, role);
    }
}
