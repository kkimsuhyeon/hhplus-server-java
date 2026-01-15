package kr.hhplus.be.server.domain.auth.application.dto.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpCommand {
    private String email;
    private String password;
}
