package kr.hhplus.be.server.domain.concert.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.application.dto.query.FindConcertQuery;
import kr.hhplus.be.server.domain.concert.exception.ConcertErrorCode;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.port.ConcertCriteria;
import kr.hhplus.be.server.domain.concert.port.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConcertQueryService {

    private final ConcertRepository repository;

    @Transactional(readOnly = true)
    public Concert getConcert(String concertId) {
        return repository.findById(concertId)
                .orElseThrow(() -> new BusinessException(ConcertErrorCode.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<Concert> getConcerts(FindConcertQuery query, Pageable pageable) {
        ConcertCriteria criteria = query == null ? ConcertCriteria.empty() : query.toCriteria();

        return repository.findAllByCriteria(criteria, pageable);
    }
}
