package kr.hhplus.be.server.domain.concert.model.specification;

import kr.hhplus.be.server.domain.concert.model.entity.ConcertEntity;
import kr.hhplus.be.server.shared.jpa.SpecificationUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcertSpecification {

    public static Specification<ConcertEntity> likeTitle(String title) {
        return SpecificationUtils.likeIgnoreCase("title", title);
    }
}
