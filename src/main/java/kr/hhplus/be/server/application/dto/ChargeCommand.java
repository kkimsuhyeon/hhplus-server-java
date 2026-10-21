package kr.hhplus.be.server.application.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChargeCommand {
    private String userId;
    private BigDecimal amount;
}
