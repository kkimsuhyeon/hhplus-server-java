package kr.hhplus.be.server.domain.concert.adapter.out.persistence.adapter;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.entity.ConcertEntity;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.entity.ConcertScheduleEntity;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.repository.ConcertJpaRepository;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.repository.ConcertScheduleJpaRepository;
import kr.hhplus.be.server.domain.concert.application.repository.ConcertScheduleRepository;
import kr.hhplus.be.server.domain.concert.exception.ConcertScheduleErrorCode;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ConcertScheduleRepositoryAdapter implements ConcertScheduleRepository {

    private final ConcertScheduleJpaRepository jpaRepository;

    private final ConcertJpaRepository concertRepository;

    @Override
    public Optional<ConcertSchedule> findById(String id) {
        return jpaRepository.findById(id)
                .map(ConcertScheduleEntity::toModel);
    }

    @Override
    public List<ConcertSchedule> findByConcertId(String concertId) {
        return jpaRepository.findByConcertId(concertId)
                .stream()
                .map(ConcertScheduleEntity::toModel)
                .toList();
    }

    @Override
    public ConcertSchedule save(ConcertSchedule schedule) {
        ConcertEntity concertEntity = concertRepository.getReferenceById(schedule.getConcertId());
        ConcertScheduleEntity entity = ConcertScheduleEntity.create(schedule, concertEntity);

        return jpaRepository.save(entity).toModel();
    }

    @Override
    public ConcertSchedule update(ConcertSchedule schedule) {
        ConcertScheduleEntity scheduleEntity = jpaRepository.findById(schedule.getId())
                .orElseThrow(() -> new BusinessException(ConcertScheduleErrorCode.NOT_FOUND));

        scheduleEntity.update(schedule);

        return scheduleEntity.toModel();
    }
}
