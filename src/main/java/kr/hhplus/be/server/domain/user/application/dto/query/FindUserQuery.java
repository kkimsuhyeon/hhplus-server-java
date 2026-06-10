package kr.hhplus.be.server.domain.user.application.dto.query;

import kr.hhplus.be.server.domain.user.model.UserRole;
import kr.hhplus.be.server.domain.user.port.UserCriteria;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindUserQuery {

    private String email;
    private UserRole role;

    public UserCriteria toCriteria() {
        return UserCriteria.builder()
                .email(this.email)
                .role(this.role)
                .build();
    }
}
