package kr.hhplus.be.server.domain.concert.adapter.in.web.mapper;

import kr.hhplus.be.server.domain.concert.adapter.in.web.request.CreateConcertRequest;
import kr.hhplus.be.server.domain.concert.application.dto.command.CreateConcertCommand;

public class ConcertCommandMapper {

    public static CreateConcertCommand toCreateCommand(CreateConcertRequest request) {
        return CreateConcertCommand.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
    }

}
