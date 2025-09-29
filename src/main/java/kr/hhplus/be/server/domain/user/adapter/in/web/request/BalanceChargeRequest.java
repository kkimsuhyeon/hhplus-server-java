package kr.hhplus.be.server.domain.user.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@Schema(name = "BalanceChargeRequest", description = "잔액 충전 Request")
public class BalanceChargeRequest {

    @Schema(description = "충전 잔액")
    @NotNull
    private BigInteger amount;
}
