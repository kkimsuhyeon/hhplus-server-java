package kr.hhplus.be.server.domain.concert.adapter.out.persistence;

import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;
import kr.hhplus.be.server.domain.concert.model.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryAdapter implements SeatRepository {

    private final SeatJpaRepository jpaRepository;

    @Override
    public Optional<SeatEntity> findById(String id) {
        return jpaRepository.findById(id);
    }
}
