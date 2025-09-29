package kr.hhplus.be.server.domain.user.model.repository;

import kr.hhplus.be.server.domain.user.model.entity.UserEntity;

import java.util.Optional;

public interface UserRepository {

    Optional<UserEntity> findById(String id);

    UserEntity save(UserEntity user);

}
