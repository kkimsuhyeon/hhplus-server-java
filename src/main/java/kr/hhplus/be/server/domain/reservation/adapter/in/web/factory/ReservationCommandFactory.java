package kr.hhplus.be.server.domain.reservation.adapter.in.web.factory;

import kr.hhplus.be.server.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.domain.reservation.adapter.in.web.request.ReserveSeatRequest;
import org.springframework.stereotype.Component;

@Component
public class ReservationCommandFactory {

    public ReserveSeatCommand toReserveSeatCommand(ReserveSeatRequest request) {
        return ReserveSeatCommand.builder()
                .userId(request.getUserId())
                .seatId(request.getSeatId())
                .build();
    }
}
