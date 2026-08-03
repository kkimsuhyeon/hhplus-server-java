package kr.hhplus.be.server.domain.reservation.adapter.in.web.mapper;

import kr.hhplus.be.server.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.domain.reservation.adapter.in.web.request.ReserveSeatRequest;

public class ReservationCommandMapper {

    public static ReserveSeatCommand toReserveSeatCommand(ReserveSeatRequest request, String userId) {
        return ReserveSeatCommand.builder()
                .userId(userId)
                .seatId(request.getSeatId())
                .build();
    }
}
