package kr.hhplus.be.server.domain.user.adapter.in.web.response;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "UserResponse", description = "유저 정보")
public class UserResponse {

    @Schema(description = "고유값")
    private String id;

    @Schema(description = "잔고")
    private BigDecimal balance;

    public static UserResponse fromModel(User model){
        return UserResponse.builder()
                .id(model.getId())
                .balance(model.getBalance())
                .build();
    }

}
