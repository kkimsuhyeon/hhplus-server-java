package kr.hhplus.be.server.domain.user.adapter.in.web.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigInteger;

@Builder
@Schema(name = "BalanceResponse", description = "잔액 정보")
public class BalanceResponse {

    @Schema(description = "잔액")
    @JsonProperty("amount")
    private BigInteger amount;
}
