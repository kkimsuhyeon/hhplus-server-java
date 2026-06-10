package kr.hhplus.be.server.domain.user.adapter.out.persistence;

import kr.hhplus.be.server.domain.user.port.UserCriteria;
import kr.hhplus.be.server.shared.jpa.SpecificationUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSpecification {

    public static Specification<UserEntity> withCriteria(UserCriteria criteria) {
        return Specification
                .where(likeEmail(criteria))
                .and(equalRole(criteria));
    }

    private static Specification<UserEntity> likeEmail(UserCriteria criteria) {
        if (criteria == null || criteria.getEmail() == null) return null;
        return SpecificationUtils.likeIgnoreCase("email", criteria.getEmail());
    }

    private static Specification<UserEntity> equalRole(UserCriteria criteria) {
        if (criteria == null || criteria.getRole() == null) return null;
        return SpecificationUtils.equal("role", criteria.getRole());
    }

}
