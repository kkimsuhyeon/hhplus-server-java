package kr.hhplus.be.server.domain.concert.application.dto.query;

import kr.hhplus.be.server.domain.concert.port.ConcertCriteria;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindConcertQuery {
    private String title;

    public ConcertCriteria toCriteria() {
        return ConcertCriteria.builder()
                .title(this.title)
                .build();
    }
}
