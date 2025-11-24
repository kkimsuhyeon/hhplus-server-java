package kr.hhplus.be.server.domain.concert.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.application.command.CreateConcertCommand;
import kr.hhplus.be.server.domain.concert.application.mapper.ConcertMapper;
import kr.hhplus.be.server.domain.concert.application.query.FindConcertQuery;
import kr.hhplus.be.server.domain.concert.model.entity.ConcertEntity;
import kr.hhplus.be.server.domain.concert.exception.ConcertErrorCode;
import kr.hhplus.be.server.domain.concert.model.factory.ConcertCriteriaFactory;
import kr.hhplus.be.server.domain.concert.model.repository.ConcertRepository;
import kr.hhplus.be.server.domain.concert.model.service.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConcertService implements ConcertService {

    private final ConcertCriteriaFactory criteriaFactory;
    private final ConcertMapper mapper;

    private final ConcertRepository concertRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ConcertEntity> getConcerts(FindConcertQuery query, Pageable pageable) {
        ConcertCriteria criteria = criteriaFactory.fromQuery(query);
        return concertRepository.findAllByCriteria(criteria, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ConcertEntity getConcert(String concertId) {
        return concertRepository.findById(concertId)
                .orElseThrow(() -> new BusinessException(ConcertErrorCode.NOT_FOUND));
    }

    @Override
    @Transactional
    public void createConcert(CreateConcertCommand command) {
        ConcertEntity entity = mapper.toEntity(command);
        concertRepository.save(entity);
    }
}
