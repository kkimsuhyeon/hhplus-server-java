package kr.hhplus.be.server.domain.auth.adapter.in.web.mapper;

import kr.hhplus.be.server.domain.auth.adapter.in.web.request.SignInRequest;
import kr.hhplus.be.server.domain.auth.adapter.in.web.request.SignUpRequest;
import kr.hhplus.be.server.domain.auth.application.dto.command.SignInCommand;
import kr.hhplus.be.server.domain.auth.application.dto.command.SignUpCommand;

public class AuthCommandMapper {

    public static SignInCommand toSignInCommand(SignInRequest request) {
        return SignInCommand.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }

    public static SignUpCommand toSignUpCommand(SignUpRequest request) {
        return SignUpCommand.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }
}
