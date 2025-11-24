package kr.hhplus.be.server.domain.concert.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 순수 도메인 모델 - Concert
 */
@Getter
@Builder
@AllArgsConstructor
public class Concert {
    
    private String id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 스케줄 ID 목록
    @Builder.Default
    private List<String> scheduleIds = new ArrayList<>();
    
    /**
     * 예약 가능 여부 확인 (스케줄이 있는지만 체크)
     */
    public boolean hasSchedules() {
        return !scheduleIds.isEmpty();
    }
}
