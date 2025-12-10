package kr.hhplus.be.server.domain.reservation.adapter.out.persistence;

import java.util.Optional;

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

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryAdapter implements ReservationRepository {

    private final ReservationJpaRepository jpaRepository;

    private final UserJpaRepository userRepository;
    private final SeatJpaRepository seatRepository;

    @Override
    public Optional<Reservation> findById(String id) {
        return jpaRepository.findById(id)
                .map(ReservationEntity::toModel);
    }

    @Override
    public Reservation save(Reservation reservation) {
        UserEntity userEntity = userRepository.getReferenceById(reservation.getUserId());
        SeatEntity seatEntity = seatRepository.getReferenceById(reservation.getSeatId());

        ReservationEntity entity = ReservationEntity.create(reservation, userEntity, seatEntity);
        ReservationEntity savedEntity = jpaRepository.save(entity);

        return savedEntity.toModel();
    }

    @Override
    public Reservation update(Reservation reservation) {
        ReservationEntity entity = jpaRepository.findById(reservation.getId())
                .orElseThrow(() -> new BusinessException(ReservationErrorCode.NOT_FOUND));

        entity.update(reservation);

        return entity.toModel();
    }

}
