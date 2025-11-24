package kr.hhplus.be.server.domain.reservation.adapter.persistence;

import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.domain.reservation.model.entity.ReservationEntity;
import org.springframework.stereotype.Component;

/**
 * JPA Entity와 도메인 Model 간의 변환을 담당
 */
@Component
public class ReservationMapper {

    /**
     * JPA Entity → 도메인 Model 변환
     */
    public Reservation toDomain(ReservationEntity entity) {
        return Reservation.builder()
                .id(entity.getId())
                .status(convertStatus(entity.getStatus()))
                .expiredAt(entity.getExpiredAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .userId(entity.getUser().getId())
                .seatId(entity.getSeat().getId())
                .seatPrice(entity.getSeat().getPrice())
                .build();
    }

    /**
     * 기존 Entity의 ReservationStatus → 새 도메인 Model의 ReservationStatus 변환
     */
    private ReservationStatus convertStatus(kr.hhplus.be.server.domain.reservation.model.entity.ReservationStatus entityStatus) {
        return switch (entityStatus) {
            case PENDING_PAYMENT -> ReservationStatus.PENDING_PAYMENT;
            case CONFIRMED -> ReservationStatus.CONFIRMED;
            case CANCELLED -> ReservationStatus.CANCELLED;
        };
    }
}
