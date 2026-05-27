package kr.hhplus.be.server.domain.concert.application.service;

import kr.hhplus.be.server.domain.concert.application.assembler.ConcertAssembler;
import kr.hhplus.be.server.domain.concert.application.dto.command.CreateConcertCommand;
import kr.hhplus.be.server.domain.concert.application.repository.ConcertRepository;
import kr.hhplus.be.server.domain.concert.model.Concert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConcertCommandService {

    private final ConcertRepository repository;

    @Transactional
    public Concert create(CreateConcertCommand command) {
        Concert concert = ConcertAssembler.toModel(command);
        return repository.save(concert);
    }

    @Transactional
    public Concert save(Concert concert) {
        return repository.save(concert);
    }
}
