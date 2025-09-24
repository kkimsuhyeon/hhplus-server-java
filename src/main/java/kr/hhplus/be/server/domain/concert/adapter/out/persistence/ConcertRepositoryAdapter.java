package kr.hhplus.be.server.domain.concert.adapter.out.persistence;

import kr.hhplus.be.server.domain.concert.model.entity.ConcertEntity;
import kr.hhplus.be.server.domain.concert.model.repository.ConcertCriteria;
import kr.hhplus.be.server.domain.concert.model.repository.ConcertRepository;
import kr.hhplus.be.server.domain.concert.model.specification.ConcertSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryAdapter implements ConcertRepository {

    private final ConcertJpaRepository jpaRepository;

    @Override
    public Page<ConcertEntity> findAllByCriteria(ConcertCriteria criteria, Pageable pageable) {
        Specification<ConcertEntity> spc = ConcertSpecification.likeTitle(criteria.getTitle());

        return jpaRepository.findAll(spc, pageable);
    }

    @Override
    public ConcertEntity save(ConcertEntity entity) {
        return jpaRepository.save(entity);
    }
}
