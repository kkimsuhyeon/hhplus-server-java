package kr.hhplus.be.server.domain.reservation.adapter.out.persistence;

import kr.hhplus.be.server.domain.reservation.model.entity.ReservationEntity;
import kr.hhplus.be.server.domain.reservation.model.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryAdapter implements ReservationRepository {

    private final ReservationJpaRepository jpaRepository;

    @Override
    public Optional<ReservationEntity> findById(String id) {
        return jpaRepository.findById(id);
    }

    @Override
    public ReservationEntity save(ReservationEntity entity) {
        return jpaRepository.save(entity);
    }
}
