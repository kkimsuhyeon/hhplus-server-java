package kr.hhplus.be.server.domain.concert.adapter.in.web.factory;

import kr.hhplus.be.server.domain.concert.adapter.in.web.request.CreateConcertRequest;
import kr.hhplus.be.server.domain.concert.application.command.CreateConcertCommand;
import org.springframework.stereotype.Component;

@Component
public class ConcertCommandFactory {

    public CreateConcertCommand toCreateCommand(CreateConcertRequest request) {
        return CreateConcertCommand.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
    }

}
