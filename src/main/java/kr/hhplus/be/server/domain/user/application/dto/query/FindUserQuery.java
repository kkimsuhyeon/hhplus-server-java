package kr.hhplus.be.server.domain.user.application.dto.query;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindUserQuery {

    private String name;
    private String email;
}
