package kr.hhplus.be.server.domain.concert.application.assembler;

import kr.hhplus.be.server.domain.concert.application.dto.command.CreateConcertCommand;
import kr.hhplus.be.server.domain.concert.model.Concert;

public class ConcertAssembler {

    public static Concert toModel(CreateConcertCommand command) {
        return Concert.builder()
                .title(command.getTitle())
                .description(command.getDescription())
                .build();
    }
}
