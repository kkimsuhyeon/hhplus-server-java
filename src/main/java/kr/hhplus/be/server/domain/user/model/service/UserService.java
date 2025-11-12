package kr.hhplus.be.server.domain.user.model.service;

import kr.hhplus.be.server.domain.user.application.query.FindUserQuery;
import kr.hhplus.be.server.domain.user.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;

public interface UserService {

    Page<UserEntity> getUsers(FindUserQuery query, Pageable pageable);

    UserEntity getUser(String id);

    void create(UserEntity user);

    void addBalance(String userId, BigInteger amount);

    void deductBalance(String userId, BigInteger amount);
}
