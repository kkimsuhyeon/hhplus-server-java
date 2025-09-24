package kr.hhplus.be.server.domain.concert.application.mapper;

import kr.hhplus.be.server.domain.concert.application.command.CreateConcertCommand;
import kr.hhplus.be.server.domain.concert.model.entity.ConcertEntity;
import org.springframework.stereotype.Component;

@Component
public class ConcertMapper {

    public ConcertEntity toEntity(CreateConcertCommand command) {
        return ConcertEntity.builder()
                .title(command.getTitle())
                .description(command.getDescription())
                .build();
    }
}
