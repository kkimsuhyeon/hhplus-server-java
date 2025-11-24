package kr.hhplus.be.server.domain.concert.adapter.persistence;

import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
import kr.hhplus.be.server.domain.concert.model.entity.ConcertScheduleEntity;
import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * JPA Entity와 도메인 Model 간의 변환을 담당
 */
@Component
public class ConcertScheduleMapper {

    /**
     * JPA Entity → 도메인 Model 변환
     */
    public ConcertSchedule toDomain(ConcertScheduleEntity entity) {
        return ConcertSchedule.builder()
                .id(entity.getId())
                .concertDate(entity.getConcertDate())
                .concertId(entity.getConcert().getId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .seatIds(entity.getSeats().stream()
                        .map(SeatEntity::getId)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * 도메인 Model의 변경사항을 JPA Entity에 반영
     */
    public void updateEntity(ConcertScheduleEntity entity, ConcertSchedule domain) {
        entity.updateFromDomain(domain.getConcertDate());
    }
}
