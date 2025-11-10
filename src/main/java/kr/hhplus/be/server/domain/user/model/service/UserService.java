package kr.hhplus.be.server.domain.user.model.service;

import kr.hhplus.be.server.domain.user.model.entity.UserEntity;

import java.math.BigInteger;

public interface UserService {

    UserEntity getUser(String id);

    void create(UserEntity user);

    void addBalance(String userId, BigInteger amount);

    void deductBalance(String userId, BigInteger amount);
}
