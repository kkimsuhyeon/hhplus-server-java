package kr.hhplus.be.server.domain.user.model.factory;

import kr.hhplus.be.server.domain.user.application.query.FindUserQuery;
import kr.hhplus.be.server.domain.user.model.repository.UserCriteria;
import org.springframework.stereotype.Component;

@Component
public class UserCriteriaFactory {

    public UserCriteria fromQuery(FindUserQuery query) {
        return UserCriteria.builder().build();
    }
}
