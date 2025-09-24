package kr.hhplus.be.server.domain.concert.application.query;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindConcertQuery {
    private String title;
}
