package kr.hhplus.be.server.domain.concert.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.application.dto.command.CreateConcertCommand;
import kr.hhplus.be.server.domain.concert.application.dto.criteria.ConcertCriteria;
import kr.hhplus.be.server.domain.concert.application.dto.mapper.ConcertMapper;
import kr.hhplus.be.server.domain.concert.application.dto.query.FindConcertQuery;
import kr.hhplus.be.server.domain.concert.application.repository.ConcertRepository;
import kr.hhplus.be.server.domain.concert.exception.ConcertErrorCode;
import kr.hhplus.be.server.domain.concert.model.Concert;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository repository;

    @Transactional(readOnly = true)
    public Concert getConcert(String concertId) {
        return repository.findById(concertId)
                .orElseThrow(() -> new BusinessException(ConcertErrorCode.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<Concert> getConcerts(FindConcertQuery query, Pageable pageable) {
        ConcertCriteria criteria = ConcertCriteria.from(query);

        return repository.findAllByCriteria(criteria, pageable);
    }

    @Transactional
    public Concert create(CreateConcertCommand command) {
        Concert concert = ConcertMapper.toModel(command);
        return repository.save(concert);
    }

    @Transactional
    public Concert save(Concert concert) {
        return repository.save(concert);
    }
}
