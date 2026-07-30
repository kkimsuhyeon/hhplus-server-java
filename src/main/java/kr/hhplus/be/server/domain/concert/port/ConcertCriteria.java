package kr.hhplus.be.server.domain.concert.port;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConcertCriteria {

    private String title;

    public static ConcertCriteria empty() {
        return ConcertCriteria.builder().build();
    }
}
