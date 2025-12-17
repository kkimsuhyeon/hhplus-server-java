package kr.hhplus.be.server.domain.concert.application.dto.command;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CreateScheduleCommand {

    private String concertId;

    private LocalDateTime concertDate;

}
