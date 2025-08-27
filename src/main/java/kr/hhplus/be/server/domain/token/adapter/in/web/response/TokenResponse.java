package kr.hhplus.be.server.domain.token.adapter.in.web.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.token.model.entity.TokenStatus;

import java.time.LocalDateTime;

@Schema(name = "TokenResponse", description = "토큰 응답 정보")
public class TokenResponse {

    @Schema(description = "대기열 토큰 ID", example = "string")
    @JsonProperty("token")
    private String token;

    @Schema(description = "토큰 만료 시간", example = "2024-01-01 15:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiredAt;

    @Schema(description = "대기열 순번 (1부터 시작)", example = "42")
    @JsonProperty("rank")
    private Long rank;

    @Schema(description = "토큰 상태", example = "WAITING")
    @JsonProperty("status")
    private TokenStatus status;
}
