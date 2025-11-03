package kr.hhplus.be.server.domain.concert.model.service;

import kr.hhplus.be.server.domain.concert.application.command.CreateConcertCommand;
import kr.hhplus.be.server.domain.concert.application.query.FindConcertQuery;
import kr.hhplus.be.server.domain.concert.model.entity.ConcertEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConcertService {

    Page<ConcertEntity> getConcerts(FindConcertQuery query, Pageable pageable);

    ConcertEntity getConcert(String concertId);

    void createConcert(CreateConcertCommand command);
}
