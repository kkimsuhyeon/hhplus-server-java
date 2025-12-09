package kr.hhplus.be.server.domain.user.adapter.out.persistence;

import kr.hhplus.be.server.shared.jpa.SpecificationUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSpecification {

    public static Specification<UserJpaEntity> likeId(String id) {
        return SpecificationUtils.likeIgnoreCase("id", id);
    }

}
