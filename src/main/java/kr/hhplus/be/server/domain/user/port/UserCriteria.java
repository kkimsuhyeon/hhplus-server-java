package kr.hhplus.be.server.domain.user.port;

import kr.hhplus.be.server.domain.user.model.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCriteria {

    private String email;
    private UserRole role;

    public static UserCriteria empty() {
        return UserCriteria.builder().build();
    }

}
