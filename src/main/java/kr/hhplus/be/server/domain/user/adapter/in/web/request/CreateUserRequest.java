package kr.hhplus.be.server.domain.user.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "CreateUserRequest", description = "유저 생성용 Request")
public class CreateUserRequest {

    private String name;
    private String email;
    private String password;
}
