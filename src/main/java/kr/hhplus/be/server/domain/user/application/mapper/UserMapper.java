package kr.hhplus.be.server.domain.user.application.mapper;

import kr.hhplus.be.server.domain.user.application.command.CreateUserCommand;
import kr.hhplus.be.server.domain.user.model.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(CreateUserCommand command) {
        return UserEntity.builder()
                .build();
    }

}
