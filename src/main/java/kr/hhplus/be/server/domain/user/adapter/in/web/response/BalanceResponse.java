package kr.hhplus.be.server.domain.user.adapter.in.web.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BalanceResponse", description = "잔액 정보")
public class BalanceResponse {

    @Schema(description = "잔액")
    @JsonProperty("amount")
    private Long amount;
}
