package kr.hhplus.be.server.domain.user.application.dto;

import kr.hhplus.be.server.domain.user.application.dto.query.FindUserQuery;
import kr.hhplus.be.server.domain.user.model.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCriteria {

    private String email;
    private UserRole role;

    public static UserCriteria from(FindUserQuery query) {
        if (query == null) return UserCriteria.empty();

        return UserCriteria.builder()
                .email(query.getEmail())
                .role(query.getRole())
                .build();
    }

    public static UserCriteria empty() {
        return UserCriteria.builder().build();
    }

}
