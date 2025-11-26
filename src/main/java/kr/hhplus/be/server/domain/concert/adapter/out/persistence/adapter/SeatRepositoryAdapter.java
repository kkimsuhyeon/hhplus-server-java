package kr.hhplus.be.server.domain.concert.adapter.out.persistence.adapter;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.entity.ConcertScheduleEntity;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.entity.SeatEntity;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.repository.ConcertScheduleJpaRepository;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.repository.SeatJpaRepository;
import kr.hhplus.be.server.domain.concert.application.repository.SeatRepository;
import kr.hhplus.be.server.domain.concert.exception.SeatErrorCode;
import kr.hhplus.be.server.domain.concert.model.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryAdapter implements SeatRepository {

    private final SeatJpaRepository jpaRepository;

    private final ConcertScheduleJpaRepository concertScheduleRepository;

    @Override
    public Optional<Seat> findById(String id) {
        return jpaRepository.findById(id)
                .map(SeatEntity::toModel);
    }

    @Override
    public Optional<Seat> findByIdForUpdate(String id) {
        return jpaRepository.findByIdForUpdate(id)
                .map(SeatEntity::toModel);
    }

    @Override
    public List<Seat> findByScheduleId(String scheduleId) {
        return jpaRepository.findBySchedule_Id(scheduleId)
                .stream()
                .map(SeatEntity::toModel)
                .toList();
    }

    @Override
    public Seat save(Seat seat) {
        ConcertScheduleEntity scheduleEntity = concertScheduleRepository.getReferenceById(seat.getScheduleId());

        SeatEntity entity = SeatEntity.create(seat, scheduleEntity);
        return jpaRepository.save(entity).toModel();
    }

    @Override
    public Seat update(Seat seat) {
        SeatEntity seatEntity = jpaRepository.findByIdForUpdate(seat.getId())
                .orElseThrow(() -> new BusinessException(SeatErrorCode.NOT_FOUND));

        seatEntity.update(seat);

        return seatEntity.toModel();
    }
}
