package kr.hhplus.be.server.domain.concert.model.factory;

import kr.hhplus.be.server.domain.concert.application.query.FindConcertQuery;
import kr.hhplus.be.server.domain.concert.model.repository.ConcertCriteria;
import org.springframework.stereotype.Component;

@Component
public class ConcertCriteriaFactory {

    public ConcertCriteria fromQuery(FindConcertQuery query) {
        return ConcertCriteria.builder()
                .title(query.getTitle())
                .build();
    }
}
