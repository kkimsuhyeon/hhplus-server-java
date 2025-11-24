package kr.hhplus.be.server.domain.concert.adapter.persistence;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.domain.concert.application.repository.SeatRepository;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SeatRepository의 JPA 구현체
 * JPA Entity와 순수 도메인 Model 간의 변환을 담당
 */
@Repository
@RequiredArgsConstructor
public class SeatRepositoryAdapter implements SeatRepository {

    private final SeatJpaRepository jpaRepository;
    private final SeatMapper mapper;

    @Override
    public Seat findById(String id) {
        SeatEntity entity = jpaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));

        return mapper.toDomain(entity);
    }

    /**
     * 비관적 락을 사용한 좌석 조회
     */
    public Seat findByIdForUpdate(String id) {
        SeatEntity entity = jpaRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));

        return mapper.toDomain(entity);
    }

    @Override
    public List<Seat> findByScheduleId(String scheduleId) {
        return jpaRepository.findByScheduleId(scheduleId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Seat save(Seat seat) {
        if (seat.getId() != null) {
            // 수정: 기존 Entity를 조회해서 업데이트
            SeatEntity entity = jpaRepository.findById(seat.getId())
                    .orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));

            // 도메인 Model의 변경사항을 Entity에 반영
            mapper.updateEntity(entity, seat);

            // 더티 체킹으로 자동 저장됨
        } else {
            // 신규 생성은 현재 지원하지 않음 (기존 Entity와의 호환성 이슈)
            throw new UnsupportedOperationException("새 좌석 생성은 아직 지원되지 않습니다.");
        }

        return seat;
    }
}
