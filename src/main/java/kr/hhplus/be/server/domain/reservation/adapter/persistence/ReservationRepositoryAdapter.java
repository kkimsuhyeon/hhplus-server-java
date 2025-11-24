package kr.hhplus.be.server.domain.reservation.adapter.persistence;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;
import kr.hhplus.be.server.domain.reservation.application.ReservationRepository;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.domain.reservation.model.entity.ReservationEntity;
import kr.hhplus.be.server.domain.user.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * Repository의 JPA 구현체
 * JPA Entity와 순수 도메인 Model 간의 변환을 담당
 */
@Repository
@RequiredArgsConstructor
public class ReservationRepositoryAdapter implements ReservationRepository {

    private final ReservationJpaRepository jpaRepository;
    private final EntityManager entityManager;
    private final ReservationMapper mapper;

    @Override
    public Reservation findById(String id) {
        ReservationEntity entity = findEntityById(id);
        return mapper.toDomain(entity);
    }

    @Override
    public Reservation save(Reservation reservation) {
        if (reservation.getId() != null) {
            // 수정: 기존 Entity를 조회해서 업데이트
            ReservationEntity entity = findEntityById(reservation.getId());

            // 도메인 Model의 변경사항을 Entity에 반영
            if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
                entity.completePayment();
            }

            // 더티 체킹으로 자동 저장됨
            return reservation;
        } else {
            // 신규: 새 Entity 생성
            UserEntity user = entityManager.getReference(UserEntity.class, reservation.getUserId());
            SeatEntity seat = entityManager.getReference(SeatEntity.class, reservation.getSeatId());

            ReservationEntity entity = ReservationEntity.create(user, seat);
            entityManager.persist(entity);

            // ID가 생성된 후 반환
            return Reservation.builder()
                    .id(entity.getId())
                    .status(convertStatus(entity.getStatus()))
                    .expiredAt(entity.getExpiredAt())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .userId(reservation.getUserId())
                    .seatId(reservation.getSeatId())
                    .seatPrice(reservation.getSeatPrice())
                    .build();
        }
    }

    private ReservationEntity findEntityById(String id) {
        return entityManager.find(ReservationEntity.class, id);
    }

    private ReservationStatus convertStatus(kr.hhplus.be.server.domain.reservation.model.entity.ReservationStatus entityStatus) {
        return switch (entityStatus) {
            case PENDING_PAYMENT -> ReservationStatus.PENDING_PAYMENT;
            case CONFIRMED -> ReservationStatus.CONFIRMED;
            case CANCELLED -> ReservationStatus.CANCELLED;
        };
    }
}
