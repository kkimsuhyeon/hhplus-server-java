package kr.hhplus.be.server.domain.concert.adapter.out.persistence;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.application.ConcertScheduleRepository;
import kr.hhplus.be.server.domain.concert.exception.ConcertErrorCode;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ConcertScheduleRepositoryAdapter implements ConcertScheduleRepository {

    private final ConcertScheduleJpaRepository jpaRepository;

    @Override
    public ConcertSchedule findById(String id) {
        return jpaRepository.findById(id)
                .map(ConcertScheduleJpaEntity::toModel)
                .orElseThrow(() -> new BusinessException(ConcertErrorCode.NOT_FOUND));
    }

    @Override
    public List<ConcertSchedule> findByConcertId(String concertId) {
        return jpaRepository.findByConcertId(concertId)
                .stream()
                .map(ConcertScheduleJpaEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public ConcertSchedule save(ConcertSchedule schedule) {
        if (schedule.getId() != null) {
            ConcertScheduleJpaEntity entity = jpaRepository.findById(schedule.getId())
                    .orElseThrow(() -> new BusinessException(ConcertErrorCode.NOT_FOUND));

            entity.update(schedule);

            return entity.toModel();
        } else {
            ConcertScheduleJpaEntity entity = ConcertScheduleJpaEntity.create(schedule);
            ConcertScheduleJpaEntity savedEntity = jpaRepository.save(entity);

            return savedEntity.toModel();
        }
    }
}
