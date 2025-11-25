package kr.hhplus.be.server.domain.concert.application.dto.query;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindConcertQuery {
    private String title;
}
