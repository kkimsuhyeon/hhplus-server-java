package kr.hhplus.be.server.domain.concert.adapter.persistence;

import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.concert.model.SeatStatus;
import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;
import org.springframework.stereotype.Component;

/**
 * JPA Entity와 도메인 Model 간의 변환을 담당
 */
@Component
public class SeatMapper {

    /**
     * JPA Entity → 도메인 Model 변환
     */
    public Seat toDomain(SeatEntity entity) {
        return Seat.builder()
                .id(entity.getId())
                .seatNumber(entity.getSeatNumber())
                .status(convertStatus(entity.getStatus()))
                .price(entity.getPrice())
                .scheduleId(entity.getSchedule().getId())
                .build();
    }

    /**
     * 도메인 Model의 변경사항을 JPA Entity에 반영
     */
    public void updateEntity(SeatEntity entity, Seat domain) {
        entity.updateFromDomain(convertToEntityStatus(domain.getStatus()));
    }

    /**
     * 기존 Entity의 SeatStatus → 새 도메인 Model의 SeatStatus 변환
     */
    private SeatStatus convertStatus(kr.hhplus.be.server.domain.concert.model.entity.SeatStatus entityStatus) {
        return switch (entityStatus) {
            case AVAILABLE -> SeatStatus.AVAILABLE;
            case RESERVING -> SeatStatus.RESERVING;
            case RESERVED -> SeatStatus.RESERVED;
        };
    }

    /**
     * 새 도메인 Model의 SeatStatus → 기존 Entity의 SeatStatus 변환
     */
    private kr.hhplus.be.server.domain.concert.model.entity.SeatStatus convertToEntityStatus(SeatStatus domainStatus) {
        return switch (domainStatus) {
            case AVAILABLE -> kr.hhplus.be.server.domain.concert.model.entity.SeatStatus.AVAILABLE;
            case RESERVING -> kr.hhplus.be.server.domain.concert.model.entity.SeatStatus.RESERVING;
            case RESERVED -> kr.hhplus.be.server.domain.concert.model.entity.SeatStatus.RESERVED;
        };
    }
}
