package kr.hhplus.be.server.domain.user.adapter.out.persistence;

import kr.hhplus.be.server.domain.user.application.UserRepository;
import kr.hhplus.be.server.domain.user.application.dto.UserCriteria;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public Page<User> findAllByCriteria(UserCriteria criteria, Pageable pageable) {
        Specification<UserJpaEntity> userJpaEntitySpecification = UserSpecification.likeId("");
        return jpaRepository.findAll(userJpaEntitySpecification, pageable)
                .map(UserJpaEntity::toModel);
    }

    @Override
    public Optional<User> findById(String id) {
        return jpaRepository.findById(id)
                .map(UserJpaEntity::toModel);
    }

    @Override
    public User save(User user) {
        return null;
    }

}
