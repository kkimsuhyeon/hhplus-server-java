package kr.hhplus.be.server.domain.concert.adapter.persistence;

import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.entity.ConcertEntity;
import kr.hhplus.be.server.domain.concert.model.entity.ConcertScheduleEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * JPA Entity와 도메인 Model 간의 변환을 담당
 */
@Component
public class ConcertMapper {

    /**
     * JPA Entity → 도메인 Model 변환
     */
    public Concert toDomain(ConcertEntity entity) {
        return Concert.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .scheduleIds(entity.getSchedules().stream()
                        .map(ConcertScheduleEntity::getId)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * 도메인 Model의 변경사항을 JPA Entity에 반영
     */
    public void updateEntity(ConcertEntity entity, Concert domain) {
        entity.updateFromDomain(domain.getTitle(), domain.getDescription());
    }
}
