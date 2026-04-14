package kr.hhplus.be.server.domain.user.application;

import kr.hhplus.be.server.domain.user.application.dto.command.CreateUserCommand;
import kr.hhplus.be.server.domain.user.model.User;

public class UserMapper {

    public static User toModel(CreateUserCommand command) {
        return User.create(command.getEmail(), command.getPassword());
    }

}
