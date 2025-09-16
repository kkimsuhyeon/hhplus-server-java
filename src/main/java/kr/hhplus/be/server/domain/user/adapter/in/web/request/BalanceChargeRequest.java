package kr.hhplus.be.server.domain.user.adapter.in.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "BalanceChargeRequest", description = "잔액 충전 Request")
public class BalanceChargeRequest {

    @Schema(description = "충전 잔액")
    @JsonProperty("amount")
    @NotNull
    private Long amount;
}
