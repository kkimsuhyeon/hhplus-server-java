package kr.hhplus.be.server.domain.concert.adapter.out.persistence;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.application.ConcertRepository;
import kr.hhplus.be.server.domain.concert.exception.ConcertErrorCode;
import kr.hhplus.be.server.domain.concert.model.Concert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryAdapter implements ConcertRepository {

    private final ConcertJpaRepository jpaRepository;

    @Override
    public Concert findById(String id) {
        return jpaRepository.findById(id)
                .map(ConcertJpaEntity::toModel)
                .orElseThrow(() -> new BusinessException(ConcertErrorCode.NOT_FOUND));
    }

    @Override
    public List<Concert> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(ConcertJpaEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Concert save(Concert concert) {
        if (concert.getId() != null) {
            ConcertJpaEntity entity = jpaRepository.findById(concert.getId())
                    .orElseThrow(() -> new BusinessException(ConcertErrorCode.NOT_FOUND));

            entity.update(concert);

            return entity.toModel();
        } else {
            ConcertJpaEntity entity = ConcertJpaEntity.create(concert);
            ConcertJpaEntity savedEntity = jpaRepository.save(entity);

            return savedEntity.toModel();
        }
    }
}
