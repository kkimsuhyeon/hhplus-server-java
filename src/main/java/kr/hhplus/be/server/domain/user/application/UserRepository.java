package kr.hhplus.be.server.domain.user.application;

import kr.hhplus.be.server.domain.user.model.User;

/**
 * User Repository 인터페이스
 * 순수 도메인 모델(User)만 다루며, JPA에 대해 전혀 알지 못함
 */
public interface UserRepository {
    
    /**
     * ID로 사용자 조회
     */
    User findById(String id);
    
    /**
     * 사용자 저장 (생성 및 수정)
     */
    User save(User user);
}
