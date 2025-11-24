package kr.hhplus.be.server.domain.user.adapter.in.web.mapper;

import kr.hhplus.be.server.domain.user.adapter.in.web.request.FindUserRequest;
import kr.hhplus.be.server.domain.user.application.dto.query.FindUserQuery;
import org.springframework.stereotype.Component;

@Component
public class UserQueryMapper {

    public FindUserQuery toFindQuery(FindUserRequest request){
        return FindUserQuery.builder().build();
    }
}
