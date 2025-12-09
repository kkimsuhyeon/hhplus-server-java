package kr.hhplus.be.server.domain.token.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IssueTokenRequest {

    @Schema(description = "유저 아이디")
    @NotBlank
    private String userId;
}
