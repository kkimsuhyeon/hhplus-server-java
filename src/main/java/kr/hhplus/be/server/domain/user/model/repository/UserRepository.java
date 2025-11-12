package kr.hhplus.be.server.domain.user.model.repository;

import kr.hhplus.be.server.domain.user.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepository {

    Page<UserEntity> findAllByCriteria(UserCriteria criteria, Pageable pageable);

    Optional<UserEntity> findById(String id);

    UserEntity save(UserEntity user);

}
