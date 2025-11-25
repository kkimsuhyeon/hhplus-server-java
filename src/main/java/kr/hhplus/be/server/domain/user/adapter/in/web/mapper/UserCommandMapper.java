package kr.hhplus.be.server.domain.user.adapter.in.web.mapper;

import kr.hhplus.be.server.domain.user.adapter.in.web.request.BalanceChargeRequest;
import kr.hhplus.be.server.domain.user.adapter.in.web.request.CreateUserRequest;
import kr.hhplus.be.server.domain.user.application.dto.command.BalanceChargeCommand;
import kr.hhplus.be.server.domain.user.application.dto.command.CreateUserCommand;

public class UserCommandMapper {

    public static CreateUserCommand toCreateCommand(CreateUserRequest request) {
        return CreateUserCommand.builder().build();
    }

    public static BalanceChargeCommand toChargeCommand(String userId, BalanceChargeRequest request) {
        return BalanceChargeCommand.builder()
                .userId(userId)
                .balance(request.getAmount())
                .build();
    }

}
