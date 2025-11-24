package kr.hhplus.be.server.domain.concert.adapter.out.persistence;

import kr.hhplus.be.server.domain.concert.model.entity.ConcertEntity;
import kr.hhplus.be.server.domain.concert.model.repository.ConcertRepository;
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
    public Page<ConcertEntity> findAllByCriteria(ConcertCriteria criteria, Pageable pageable) {
        Specification<ConcertEntity> spc = ConcertSpecification.likeTitle(criteria.getTitle());

        return jpaRepository.findAll(spc, pageable);
    }

    @Override
    public Optional<ConcertEntity> findById(String concertId) {
        return jpaRepository.findWithSchedulesById(concertId);
    }

    @Override
    public ConcertEntity save(ConcertEntity entity) {
        return jpaRepository.save(entity);
    }
}
