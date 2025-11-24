package kr.hhplus.be.server.domain.user.application.dto;

import kr.hhplus.be.server.domain.user.application.dto.query.FindUserQuery;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCriteria {

    private String name;
    private String email;

    public static UserCriteria from(FindUserQuery query) {
        if (query == null) return UserCriteria.empty();

        return UserCriteria.builder()
                .name(query.getName())
                .email(query.getEmail())
                .build();
    }

    public static UserCriteria empty() {
        return UserCriteria.builder().build();
    }

}
