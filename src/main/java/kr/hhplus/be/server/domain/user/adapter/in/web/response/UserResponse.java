package kr.hhplus.be.server.domain.user.adapter.in.web.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.user.model.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@Builder
@Schema(name = "UserResponse", description = "유저 정보")
public class UserResponse {

    @Schema(description = "고유값")
    private String id;

    @Schema(description = "잔고")
    private BigInteger balance;

    public static UserResponse fromEntity(UserEntity entity){
        return UserResponse.builder()
                .id(entity.getId())
                .balance(entity.getBalance())
                .build();
    }

}
