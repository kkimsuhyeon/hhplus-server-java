package kr.hhplus.be.server.domain.concert.application.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CreateSeatCommand {

    private BigDecimal price;

    private String scheduleId;
}
