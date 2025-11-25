package kr.hhplus.be.server.domain.user.adapter.in.web.response;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "BalanceResponse", description = "잔액 정보")
public class BalanceResponse {

    @Schema(description = "잔액")
    private BigDecimal amount;
}
