package kr.hhplus.be.server.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ChargeCommand {
    private String userId;
    private BigDecimal amount;
}
