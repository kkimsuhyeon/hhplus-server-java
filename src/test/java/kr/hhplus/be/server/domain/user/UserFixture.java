package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.domain.user.model.UserRole;

import java.math.BigDecimal;

public class UserFixture {

    private static final String EMAIL = "test@test.com";
    private static final String PASSWORD = "password123";

    public static User withBalance(BigDecimal balance) {
        return User.of(null, EMAIL, PASSWORD, balance, UserRole.USER);
    }
}
