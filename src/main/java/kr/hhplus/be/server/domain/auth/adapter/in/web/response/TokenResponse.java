package kr.hhplus.be.server.domain.auth.adapter.in.web.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "TokenResponse", description = "토큰 결과 Response")
public class TokenResponse {

    private String accessToken;

    private String refreshToken;

}
