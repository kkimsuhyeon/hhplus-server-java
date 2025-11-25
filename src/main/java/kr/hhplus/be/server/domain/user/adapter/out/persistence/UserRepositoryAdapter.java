package kr.hhplus.be.server.domain.user.adapter.out.persistence;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.user.application.UserRepository;
import kr.hhplus.be.server.domain.user.application.dto.UserCriteria;
import kr.hhplus.be.server.domain.user.exception.UserErrorCode;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.RequiredArgsConstructor;

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
        UserJpaEntity entity = UserJpaEntity.create(user);
        UserJpaEntity savedEntity = jpaRepository.save(entity);

        return savedEntity.toModel();
    }

    @Override
    public User update(User user) {
        UserJpaEntity entity = jpaRepository.findById(user.getId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));

        entity.update(user);
        
        return entity.toModel();
    }

}
