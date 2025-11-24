package kr.hhplus.be.server.domain.user.application;

import kr.hhplus.be.server.domain.user.application.dto.command.CreateUserCommand;
import kr.hhplus.be.server.domain.user.model.User;

import java.math.BigDecimal;

public class UserMapper {

    public static User toModel(CreateUserCommand command) {
        return User.builder()
                .balance(BigDecimal.ZERO)
                .build();
    }

}
