package kr.hhplus.be.server.domain.user.application.dto.command;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BalanceChargeCommand {
    private String userId;
    private BigDecimal balance;
}
