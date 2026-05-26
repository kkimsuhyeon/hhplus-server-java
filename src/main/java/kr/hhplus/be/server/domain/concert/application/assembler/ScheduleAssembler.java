package kr.hhplus.be.server.domain.concert.application.assembler;

import kr.hhplus.be.server.domain.concert.application.dto.command.CreateScheduleCommand;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;

public class ScheduleAssembler {

    public static ConcertSchedule toModel(CreateScheduleCommand command) {
        return ConcertSchedule.builder()
                .concertId(command.getConcertId())
                .concertDate(command.getConcertDate())
                .build();
    }
}
