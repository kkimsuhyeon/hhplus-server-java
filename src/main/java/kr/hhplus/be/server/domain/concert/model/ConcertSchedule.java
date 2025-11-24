package kr.hhplus.be.server.domain.concert.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 순수 도메인 모델 - ConcertSchedule
 */
@Getter
@Builder
@AllArgsConstructor
public class ConcertSchedule {

    private String id;
    private LocalDateTime concertDate;
    private String concertId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 좌석 ID 목록
    @Builder.Default
    private List<String> seatIds = new ArrayList<>();

    /**
     * 예약 가능 여부 확인 (좌석이 있는지만 체크)
     */
    public boolean hasSeats() {
        return !seatIds.isEmpty();
    }

    /**
     * 좌석 개수 반환
     */
    public int getSeatCount() {
        return seatIds.size();
    }
}
