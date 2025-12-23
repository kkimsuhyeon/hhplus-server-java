package kr.hhplus.be.server.domain.user.model;

import java.math.BigDecimal;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.domain.user.exception.UserErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class User {

    private String id;
    private String email;
    private BigDecimal balance;
    private UserRole role;

    public void addBalance(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT);
        }
        this.balance = this.balance.add(amount);
    }

    public void deductBalance(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new BusinessException(UserErrorCode.NOT_ENOUGH_POINT);
        }
        this.balance = this.balance.subtract(amount);
    }


    public static User create() {
        return User.builder()
                .balance(BigDecimal.ZERO)
                .build();
    }
}
