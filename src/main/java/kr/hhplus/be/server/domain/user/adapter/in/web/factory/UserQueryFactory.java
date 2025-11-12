package kr.hhplus.be.server.domain.user.adapter.in.web.factory;

import kr.hhplus.be.server.domain.user.adapter.in.web.request.FindUserRequest;
import kr.hhplus.be.server.domain.user.application.query.FindUserQuery;
import org.springframework.stereotype.Component;

@Component
public class UserQueryFactory {

    public FindUserQuery toFindQuery(FindUserRequest request){
        return FindUserQuery.builder().build();
    }
}
