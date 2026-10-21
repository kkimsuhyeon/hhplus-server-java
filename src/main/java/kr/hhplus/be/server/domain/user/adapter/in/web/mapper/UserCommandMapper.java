package kr.hhplus.be.server.domain.user.adapter.in.web.mapper;

import kr.hhplus.be.server.application.dto.ChargeCommand;
import kr.hhplus.be.server.domain.user.adapter.in.web.request.BalanceChargeRequest;
import kr.hhplus.be.server.domain.user.adapter.in.web.request.CreateUserRequest;
import kr.hhplus.be.server.domain.user.application.dto.command.CreateUserCommand;

public class UserCommandMapper {

    public static CreateUserCommand toCreateCommand(CreateUserRequest request) {
        return CreateUserCommand.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .build();
    }

    public static ChargeCommand toChargeCommand(String userId, BalanceChargeRequest request) {
        return ChargeCommand.builder()
                .userId(userId)
                .amount(request.getAmount())
                .build();
    }

}
