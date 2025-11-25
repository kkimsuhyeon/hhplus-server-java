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
public class ConcertSchedule {

    private String id;

    private LocalDateTime concertDate;

    private String concertId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder.Default
    private List<String> seatIds = new ArrayList<>();

    public boolean hasSeats() {
        return !seatIds.isEmpty();
    }

    public int getSeatCount() {
        return seatIds.size();
    }
}
