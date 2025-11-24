package kr.hhplus.be.server.domain.user.adapter.persistence;

import kr.hhplus.be.server.domain.user.model.User;
import org.springframework.stereotype.Component;

/**
 * JPA Entity와 도메인 Model 간의 변환을 담당
 */
@Component
public class UserMapper {
    
    /**
     * JPA Entity → 도메인 Model 변환
     */
    public User toDomain(UserJpaEntity entity) {
        return User.builder()
                .id(entity.getId())
                .balance(entity.getBalance())
                .build();
    }
    
    /**
     * 도메인 Model의 변경사항을 JPA Entity에 반영
     */
    public void updateEntity(UserJpaEntity entity, User domain) {
        entity.updateFromDomain(domain.getBalance());
    }
}
