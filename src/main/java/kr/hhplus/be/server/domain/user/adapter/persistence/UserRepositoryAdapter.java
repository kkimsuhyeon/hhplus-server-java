package kr.hhplus.be.server.domain.user.adapter.persistence;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.domain.user.adapter.out.persistence.UserJpaRepository;
import kr.hhplus.be.server.domain.user.application.UserRepository;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * Repository의 JPA 구현체
 * JPA Entity와 순수 도메인 Model 간의 변환을 담당
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {
    
    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;
    
    @Override
    public User findById(String id) {
        // 1. 기존 UserEntity를 조회
        kr.hhplus.be.server.domain.user.model.entity.UserEntity oldEntity = 
                jpaRepository.findById(id)
                        .orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));
        
        // 2. 임시로 기존 Entity를 새 JpaEntity로 변환
        UserJpaEntity newEntity = UserJpaEntity.builder()
                .id(oldEntity.getId())
                .balance(oldEntity.getBalance())
                .build();
        
        // 3. Entity → Domain Model 변환
        return mapper.toDomain(newEntity);
    }
    
    @Override
    public User save(User user) {
        if (user.getId() != null) {
            // 수정: 기존 Entity를 조회해서 업데이트
            kr.hhplus.be.server.domain.user.model.entity.UserEntity entity = 
                    jpaRepository.findById(user.getId())
                            .orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));
            
            // 도메인 Model의 변경사항을 Entity에 반영
            entity.addBalance(user.getBalance().subtract(entity.getBalance()));
            
            // 더티 체킹으로 자동 저장됨
        } else {
            // 신규: 새 Entity 생성
            kr.hhplus.be.server.domain.user.model.entity.UserEntity entity = 
                    kr.hhplus.be.server.domain.user.model.entity.UserEntity.builder()
                            .balance(user.getBalance())
                            .build();
            
            jpaRepository.save(entity);
        }
        
        return user;
    }
}
