package kr.hhplus.be.server.domain.user.application.command;

import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@Builder
public class BalanceChargeCommand {
    private String userId;
    private BigInteger balance;
}
