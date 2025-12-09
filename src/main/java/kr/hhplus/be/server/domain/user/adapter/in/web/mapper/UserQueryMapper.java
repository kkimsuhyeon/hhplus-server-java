package kr.hhplus.be.server.domain.user.adapter.in.web.mapper;

import kr.hhplus.be.server.domain.user.adapter.in.web.request.FindUserRequest;
import kr.hhplus.be.server.domain.user.application.dto.query.FindUserQuery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserQueryMapper {

    public static FindUserQuery toFindQuery(FindUserRequest request) {
        return FindUserQuery.builder()
                .id(request.getId())
                .name(request.getName())
                .email(request.getEmail())
                .build();
    }
}
