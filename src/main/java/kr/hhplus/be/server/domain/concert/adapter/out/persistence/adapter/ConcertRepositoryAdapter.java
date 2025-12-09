package kr.hhplus.be.server.domain.concert.adapter.out.persistence.adapter;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.ConcertSpecification;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.entity.ConcertEntity;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.repository.ConcertJpaRepository;
import kr.hhplus.be.server.domain.concert.application.dto.criteria.ConcertCriteria;
import kr.hhplus.be.server.domain.concert.application.repository.ConcertRepository;
import kr.hhplus.be.server.domain.concert.exception.ConcertErrorCode;
import kr.hhplus.be.server.domain.concert.model.Concert;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryAdapter implements ConcertRepository {

    private final ConcertJpaRepository jpaRepository;

    @Override
    public Optional<Concert> findById(String id) {
        return jpaRepository.findById(id)
                .map(ConcertEntity::toModel);
    }

    @Override
    public Page<Concert> findAllByCriteria(ConcertCriteria criteria, Pageable pageable) {
        Specification<ConcertEntity> specification = ConcertSpecification.likeTitle(criteria.getTitle());
        Page<ConcertEntity> concertEntities = jpaRepository.findAll(specification, pageable);

        return concertEntities.map(ConcertEntity::toModel);
    }

    @Override
    public Concert save(Concert concert) {
        ConcertEntity concertEntity = ConcertEntity.create(concert);

        return jpaRepository.save(concertEntity).toModel();
    }

    @Override
    public Concert update(Concert concert) {
        ConcertEntity entity = jpaRepository.findById(concert.getId())
                .orElseThrow(() -> new BusinessException(ConcertErrorCode.NOT_FOUND));

        entity.update(concert);

        return entity.toModel();
    }
}
