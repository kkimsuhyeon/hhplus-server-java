package kr.hhplus.be.server.domain.concert.adapter.out.persistence.adapter;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.entity.SeatEntity;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.repository.SeatJpaRepository;
import kr.hhplus.be.server.domain.concert.application.repository.SeatRepository;
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
                .map(SeatEntity::toModel)
                .orElseThrow(() -> new BusinessException(SeatErrorCode.NOT_FOUND));
    }

    @Override
    public Seat findByIdForUpdate(String id) {
        return jpaRepository.findByIdForUpdate(id)
                .map(SeatEntity::toModel)
                .orElseThrow(() -> new BusinessException(SeatErrorCode.NOT_FOUND));
    }

    @Override
    public List<Seat> findByScheduleId(String scheduleId) {
        return jpaRepository.findAll()
                .stream()
                .filter(entity -> entity.getScheduleId().equals(scheduleId))
                .map(SeatEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Seat save(Seat seat) {
        if (seat.getId() != null) {
            SeatEntity entity = jpaRepository.findById(seat.getId())
                    .orElseThrow(() -> new BusinessException(SeatErrorCode.NOT_FOUND));

            entity.update(seat);

            return entity.toModel();
        } else {
            SeatEntity entity = SeatEntity.create(seat);
            SeatEntity savedEntity = jpaRepository.save(entity);

            return savedEntity.toModel();
        }
    }
}
