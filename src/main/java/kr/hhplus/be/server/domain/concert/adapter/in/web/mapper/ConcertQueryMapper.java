package kr.hhplus.be.server.domain.concert.adapter.in.web.mapper;

import kr.hhplus.be.server.domain.concert.adapter.in.web.request.FindConcertRequest;
import kr.hhplus.be.server.domain.concert.application.dto.query.FindConcertQuery;

public class ConcertQueryMapper {

    public static FindConcertQuery toFindQuery(FindConcertRequest request) {
        return FindConcertQuery.builder()
                .title(request.getTitle())
                .build();
    }
}
