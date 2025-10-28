package kr.hhplus.be.server.domain.user.model.service;

import kr.hhplus.be.server.domain.user.model.entity.UserEntity;

public interface UserService {

    UserEntity getUser(String id);

    void create(UserEntity user);

}
