package kr.hhplus.be.server.domain.concert.application.dto.mapper;

import kr.hhplus.be.server.domain.concert.application.dto.command.CreateSeatCommand;
import kr.hhplus.be.server.domain.concert.model.Seat;

public class SeatMapper {

    public static Seat toModel(CreateSeatCommand command){
        return Seat.create(command.getPrice(), command.getScheduleId());
    }
}
