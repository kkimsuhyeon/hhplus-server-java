package kr.hhplus.be.server.domain.user.adapter.out.persistence;

import kr.hhplus.be.server.domain.user.model.entity.UserEntity;
import kr.hhplus.be.server.domain.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public Optional<UserEntity> findById(String id) {
        return jpaRepository.findById(id);
    }

    @Override
    public UserEntity save(UserEntity user) {
        return null;
    }
}
