package kr.hhplus.be.server.domain.concert.application.assembler;

import kr.hhplus.be.server.domain.concert.application.dto.command.CreateSeatCommand;
import kr.hhplus.be.server.domain.concert.model.Seat;

public class SeatAssembler {

    public static Seat toModel(CreateSeatCommand command) {
        return Seat.create(command.getPrice(), command.getScheduleId());
    }
}
