package kr.hhplus.be.server.domain.concert.adapter.in.web.factory;

import kr.hhplus.be.server.domain.concert.adapter.in.web.request.FindConcertRequest;
import kr.hhplus.be.server.domain.concert.application.query.FindConcertQuery;
import org.springframework.stereotype.Component;

@Component
public class ConcertQueryFactory {

    public FindConcertQuery toFindQuery(FindConcertRequest request) {
        return FindConcertQuery.builder()
                .title(request.getTitle())
                .build();
    }
}
