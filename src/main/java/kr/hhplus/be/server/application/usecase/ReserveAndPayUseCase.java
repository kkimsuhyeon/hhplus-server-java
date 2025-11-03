package kr.hhplus.be.server.application.usecase;

import kr.hhplus.be.server.application.dto.ReserveAndPayRequest;
import kr.hhplus.be.server.domain.concert.model.entity.ConcertEntity;
import kr.hhplus.be.server.domain.concert.model.service.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReserveAndPayUseCase {

    private final ConcertService concertService;

    public void execute(ReserveAndPayRequest request) {
        ConcertEntity concert = concertService.getConcert(request.getConcertId());
    }
}
