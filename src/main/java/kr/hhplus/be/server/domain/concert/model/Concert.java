package kr.hhplus.be.server.domain.concert.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class Concert {

    private String id;

    private String title;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder.Default
    private List<String> scheduleIds = new ArrayList<>();

    public boolean hasSchedules() {
        return !scheduleIds.isEmpty();
    }
}
