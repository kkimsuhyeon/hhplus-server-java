package kr.hhplus.be.server.domain.reservation.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.entity.SeatEntity;
import kr.hhplus.be.server.domain.concert.adapter.out.persistence.repository.SeatJpaRepository;
import kr.hhplus.be.server.domain.reservation.application.ReservationRepository;
import kr.hhplus.be.server.domain.reservation.exception.ReservationErrorCode;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.user.adapter.out.persistence.UserJpaEntity;
import kr.hhplus.be.server.domain.user.adapter.out.persistence.UserJpaRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryAdapter implements ReservationRepository {

    private final ReservationJpaRepository jpaRepository;

    private final UserJpaRepository userRepository;
    private final SeatJpaRepository seatRepository;

    @Override
    public Optional<Reservation> findById(String id) {
        return jpaRepository.findById(id)
                .map(ReservationJpaEntity::toModel);
    }

    @Override
    public Reservation save(Reservation reservation) {
        UserJpaEntity userEntity = userRepository.getReferenceById(reservation.getUserId());
        SeatEntity seatEntity = seatRepository.getReferenceById(reservation.getSeatId());

        ReservationJpaEntity entity = ReservationJpaEntity.create(reservation, userEntity, seatEntity);
        ReservationJpaEntity savedEntity = jpaRepository.save(entity);

        return savedEntity.toModel();
    }

    @Override
    public Reservation update(Reservation reservation) {
        ReservationJpaEntity entity = jpaRepository.findById(reservation.getId())
                .orElseThrow(() -> new BusinessException(ReservationErrorCode.NOT_FOUND));

        entity.update(reservation);

        return entity.toModel();
    }

}
