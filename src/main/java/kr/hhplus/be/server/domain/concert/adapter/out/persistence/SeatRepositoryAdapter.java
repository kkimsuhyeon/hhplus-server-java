package kr.hhplus.be.server.domain.concert.adapter.out.persistence;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.application.SeatRepository;
import kr.hhplus.be.server.domain.concert.exception.SeatErrorCode;
import kr.hhplus.be.server.domain.concert.model.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryAdapter implements SeatRepository {

    private final SeatJpaRepository jpaRepository;

    @Override
    public Seat findById(String id) {
        return jpaRepository.findById(id)
                .map(SeatJpaEntity::toModel)
                .orElseThrow(() -> new BusinessException(SeatErrorCode.NOT_FOUND));
    }

    @Override
    public Seat findByIdForUpdate(String id) {
        return jpaRepository.findByIdForUpdate(id)
                .map(SeatJpaEntity::toModel)
                .orElseThrow(() -> new BusinessException(SeatErrorCode.NOT_FOUND));
    }

    @Override
    public List<Seat> findByScheduleId(String scheduleId) {
        return jpaRepository.findAll()
                .stream()
                .filter(entity -> entity.getScheduleId().equals(scheduleId))
                .map(SeatJpaEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Seat save(Seat seat) {
        if (seat.getId() != null) {
            SeatJpaEntity entity = jpaRepository.findById(seat.getId())
                    .orElseThrow(() -> new BusinessException(SeatErrorCode.NOT_FOUND));

            entity.update(seat);

            return entity.toModel();
        } else {
            SeatJpaEntity entity = SeatJpaEntity.create(seat);
            SeatJpaEntity savedEntity = jpaRepository.save(entity);

            return savedEntity.toModel();
        }
    }
}
