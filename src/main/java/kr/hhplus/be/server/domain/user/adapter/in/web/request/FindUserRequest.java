package kr.hhplus.be.server.domain.user.adapter.in.web.request;

import kr.hhplus.be.server.domain.user.model.UserRole;
import lombok.Data;

@Data
public class FindUserRequest {

    private String email;
    private UserRole role;
}
