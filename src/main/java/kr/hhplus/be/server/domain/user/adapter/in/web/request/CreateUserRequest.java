package kr.hhplus.be.server.domain.user.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "CreateUserRequest", description = "유저 생성용 Request")
public class CreateUserRequest {

    // todo check, 추후 개발
    private String name;
    private String email;
    private String password;
}
