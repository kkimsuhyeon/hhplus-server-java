package kr.hhplus.be.server.domain.reservation.application;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CreateReservationCommand {

    private String userId;

    private String seatId;

    private BigDecimal price;
}
