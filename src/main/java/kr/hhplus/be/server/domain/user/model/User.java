package kr.hhplus.be.server.domain.user.model;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.domain.user.exception.UserErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    private String id;
    private String email;
    private String password;
    private BigDecimal balance;
    private UserRole role;

    public void addBalance(BigDecimal amount) {
        if (amount == null) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT);
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT);
        }

        this.balance = this.balance.add(amount);
    }

    public void deductBalance(BigDecimal amount) {
        if (amount == null) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT);
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT);
        }

        if (this.balance.compareTo(amount) < 0) {
            throw new BusinessException(UserErrorCode.NOT_ENOUGH_POINT);
        }

        this.balance = this.balance.subtract(amount);
    }

    public static User create(String email, String password) {
        return new User(null, email, password, BigDecimal.ZERO, UserRole.USER);
    }

    public static User of(String id, String email, String password, BigDecimal balance, UserRole role) {
        return new User(id, email, password, balance, role);
    }
}
