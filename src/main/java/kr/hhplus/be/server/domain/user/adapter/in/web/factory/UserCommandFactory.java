package kr.hhplus.be.server.domain.user.adapter.in.web.factory;

import kr.hhplus.be.server.domain.user.adapter.in.web.request.BalanceChargeRequest;
import kr.hhplus.be.server.domain.user.adapter.in.web.request.CreateUserRequest;
import kr.hhplus.be.server.domain.user.application.command.BalanceChargeCommand;
import kr.hhplus.be.server.domain.user.application.command.CreateUserCommand;
import org.springframework.stereotype.Component;

@Component
public class UserCommandFactory {

    public BalanceChargeCommand toChargeCommand(String userId, BalanceChargeRequest request) {
        return BalanceChargeCommand.builder()
                .userId(userId)
                .balance(request.getAmount())
                .build();
    }

    public CreateUserCommand toCreateCommand(CreateUserRequest request) {
        return CreateUserCommand.builder().build();
    }
}
