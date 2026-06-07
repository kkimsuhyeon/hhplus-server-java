package kr.hhplus.be.server.domain.user.application.dto.query;

import kr.hhplus.be.server.domain.user.model.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindUserQuery {

    private String email;
    private UserRole role;
}
