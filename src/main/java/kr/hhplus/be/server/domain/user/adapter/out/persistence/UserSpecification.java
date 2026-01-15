package kr.hhplus.be.server.domain.user.adapter.out.persistence;

import kr.hhplus.be.server.domain.user.application.dto.UserCriteria;
import kr.hhplus.be.server.shared.jpa.SpecificationUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSpecification {

    public static Specification<UserEntity> withCriteria(UserCriteria criteria) {
        return Specification.where(likeName(criteria));
    }

    private static Specification<UserEntity> likeName(UserCriteria criteria) {
        if (criteria == null || criteria.getName() == null) return null;
        return SpecificationUtils.likeIgnoreCase("name", criteria.getName());
    }

}
