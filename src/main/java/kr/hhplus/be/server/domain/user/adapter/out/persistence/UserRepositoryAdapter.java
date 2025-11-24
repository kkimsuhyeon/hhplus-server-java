package kr.hhplus.be.server.domain.user.adapter.out.persistence;

import kr.hhplus.be.server.domain.user.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public Page<UserEntity> findAllByCriteria(UserCriteria criteria, Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public Optional<UserEntity> findById(String id) {
        return jpaRepository.findById(id);
    }

    @Override
    public UserEntity save(UserEntity user) {
        return jpaRepository.save(user);
    }
}
