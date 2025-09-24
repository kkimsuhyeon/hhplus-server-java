package kr.hhplus.be.server.shared.jpa;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpecificationUtils {

    public static <T> Specification<T> likeIgnoreCase(String fieldName, String value) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(value)) return null;
            return criteriaBuilder.like(criteriaBuilder.lower(root.get(fieldName)), "%" + value.toLowerCase() + "%");
        };
    }
}
