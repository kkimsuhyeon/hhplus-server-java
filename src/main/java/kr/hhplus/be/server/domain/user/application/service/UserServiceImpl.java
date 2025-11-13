package kr.hhplus.be.server.domain.user.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.user.application.command.CreateUserCommand;
import kr.hhplus.be.server.domain.user.application.mapper.UserMapper;
import kr.hhplus.be.server.domain.user.application.query.FindUserQuery;
import kr.hhplus.be.server.domain.user.model.entity.UserEntity;
import kr.hhplus.be.server.domain.user.model.exception.UserErrorCode;
import kr.hhplus.be.server.domain.user.model.factory.UserCriteriaFactory;
import kr.hhplus.be.server.domain.user.model.repository.UserCriteria;
import kr.hhplus.be.server.domain.user.model.repository.UserRepository;
import kr.hhplus.be.server.domain.user.model.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserCriteriaFactory criteriaFactory;
    private final UserMapper userMapper;

    private final UserRepository repository;

    @Override
    public Page<UserEntity> getUsers(FindUserQuery query, Pageable pageable) {
        UserCriteria criteria = criteriaFactory.fromQuery(query);
        return repository.findAllByCriteria(criteria, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUser(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
    }

    @Override
    @Transactional
    public void create(CreateUserCommand command) {
        UserEntity userEntity = userMapper.toEntity(command);
        repository.save(userEntity);
    }

    @Override
    @Transactional
    public void addBalance(String userId, BigInteger amount) {
        UserEntity user = this.getUser(userId);
        user.addBalance(amount);
    }

    @Override
    @Transactional
    public void deductBalance(String userId, BigInteger amount) {
        UserEntity user = this.getUser(userId);
        user.deductBalance(amount);
    }

}
