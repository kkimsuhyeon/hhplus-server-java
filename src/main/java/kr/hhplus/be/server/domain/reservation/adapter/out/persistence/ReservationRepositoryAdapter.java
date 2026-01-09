package kr.hhplus.be.server.domain.reservation.adapter.out.persistence;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.reservation.application.ReservationRepository;
import kr.hhplus.be.server.domain.reservation.exception.ReservationErrorCode;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    public Optional<Reservation> findByUserIdAndSeatId(String userId, String seatId) {
        return jpaRepository.findByUserIdAndSeatId(userId, seatId)
                .map(ReservationEntity::toModel);
    }

    @Override
    public List<Reservation> findByUserId(String userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(ReservationEntity::toModel)
                .toList();
    }

    @Override
    public Reservation save(Reservation reservation) {
        ReservationEntity entity = ReservationEntity.create(reservation);
        ReservationEntity savedEntity = jpaRepository.save(entity);

        return savedEntity.toModel();
    }

    @Override
    public Reservation update(Reservation reservation) {
        ReservationEntity reservationEntity = jpaRepository.findByIdForUpdate(reservation.getId())
                .orElseThrow(() -> new BusinessException(ReservationErrorCode.NOT_FOUND));

        reservationEntity.update(reservation);
        return reservationEntity.toModel();

    }
}
