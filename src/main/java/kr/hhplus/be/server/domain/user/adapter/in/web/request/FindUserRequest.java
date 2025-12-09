package kr.hhplus.be.server.domain.user.adapter.in.web.request;

import lombok.Data;

@Data
public class FindUserRequest {

    private String id;
    private String name;
    private String email;
}
