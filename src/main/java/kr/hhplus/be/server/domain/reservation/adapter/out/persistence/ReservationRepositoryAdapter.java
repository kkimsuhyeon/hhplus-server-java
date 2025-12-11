package kr.hhplus.be.server.domain.reservation.adapter.out.persistence;

import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.entity.SeatEntity;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.repository.SeatJpaRepository;
import kr.hhplus.be.server.domain.reservation.application.ReservationRepository;
import kr.hhplus.be.server.domain.reservation.exception.ReservationErrorCode;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.user.adapter.out.persistence.UserEntity;
import kr.hhplus.be.server.domain.user.adapter.out.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReservationRepositoryAdapter implements ReservationRepository {

    private final ReservationJpaRepository jpaRepository;

    @Override
    public Optional<Reservation> findById(String id) {
        return jpaRepository.findById(id)
                .map(ReservationEntity::toModel);
    }

    @Override
    public Optional<Reservation> findByIdForUpdate(String id) {
        return jpaRepository.findByIdForUpdate(id)
                .map(ReservationEntity::toModel);
    }

    @Override
    public Reservation save(Reservation reservation) {
        ReservationEntity entity = ReservationEntity.create(reservation);
        ReservationEntity savedEntity = jpaRepository.save(entity);

        return savedEntity.toModel();
    }

    @Override
    public Reservation update(Reservation reservation) {
        ReservationEntity reservationEntity = jpaRepository.getReferenceById(reservation.getId());

        try {
            reservationEntity.update(reservation);
            return reservationEntity.toModel();
        } catch (EntityNotFoundException e) {
            log.error("예약 업데이트 실패: reservationId={}", reservation.getId(), e);
            throw new BusinessException(ReservationErrorCode.NOT_FOUND);
        }
    }
}
