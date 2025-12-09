package kr.hhplus.be.server.domain.concert.adapter.out.persistence.adapter;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.entity.ConcertScheduleEntity;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.entity.SeatEntity;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.repository.ConcertScheduleJpaRepository;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.repository.SeatJpaRepository;
import kr.hhplus.be.server.domain.concert.application.repository.SeatRepository;
import kr.hhplus.be.server.domain.concert.exception.SeatErrorCode;
import kr.hhplus.be.server.domain.concert.model.Seat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
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

        try {
            SeatEntity entity = SeatEntity.create(seat, scheduleEntity);
            return jpaRepository.save(entity).toModel();
        } catch (EntityNotFoundException e) {
            log.error("좌석 저장 실패: seatId={}", seat.getId(), e);
            throw new BusinessException(SeatErrorCode.NOT_FOUND);
        }

    }

    @Override
    public Seat update(Seat seat) {
        SeatEntity seatEntity = jpaRepository.getReferenceById(seat.getId());

        try {
            seatEntity.update(seat);
            return seatEntity.toModel();
        } catch (EntityNotFoundException e) {
            log.error("좌석 업데이트 실패: seatId={}", seat.getId(), e);
            throw new BusinessException(SeatErrorCode.NOT_FOUND);
        }
    }
}
