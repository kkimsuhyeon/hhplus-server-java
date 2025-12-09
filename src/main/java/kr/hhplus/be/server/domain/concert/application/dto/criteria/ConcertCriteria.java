package kr.hhplus.be.server.domain.concert.application.dto.criteria;

import kr.hhplus.be.server.domain.concert.application.dto.query.FindConcertQuery;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConcertCriteria {

    private String title;

    public static ConcertCriteria from(FindConcertQuery query) {
        if (query == null) return ConcertCriteria.empty();

        return ConcertCriteria.builder()
                .title(query.getTitle())
                .build();
    }

    public static ConcertCriteria empty() {
        return ConcertCriteria.builder().build();
    }
}
