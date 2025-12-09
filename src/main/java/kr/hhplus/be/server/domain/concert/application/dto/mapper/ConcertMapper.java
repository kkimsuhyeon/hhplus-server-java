package kr.hhplus.be.server.domain.concert.application.dto.mapper;

import kr.hhplus.be.server.domain.concert.application.dto.command.CreateConcertCommand;
import kr.hhplus.be.server.domain.concert.model.Concert;

public class ConcertMapper {

    public static Concert toModel(CreateConcertCommand command) {
        return Concert.builder()
                .title(command.getTitle())
                .description(command.getDescription())
                .build();
    }
}
