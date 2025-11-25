package kr.hhplus.be.server.domain.user.application;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.user.application.dto.UserCriteria;
import kr.hhplus.be.server.domain.user.application.dto.command.CreateUserCommand;
import kr.hhplus.be.server.domain.user.application.dto.query.FindUserQuery;
import kr.hhplus.be.server.domain.user.exception.UserErrorCode;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    @Transactional(readOnly = true)
    public Page<User> getUsers(FindUserQuery query, Pageable pageable) {
        UserCriteria criteria = UserCriteria.from(query);

        return repository.findAllByCriteria(criteria, pageable);
    }

    @Transactional(readOnly = true)
    public User getUser(String userId) {
        return findUserById(userId);
    }

    @Transactional
    public void create(CreateUserCommand command) {
        User user = UserMapper.toModel(command);
        repository.save(user);
    }

    @Transactional
    public void addBalance(String userId, BigDecimal amount) {
        User user = findUserById(userId);
        user.addBalance(amount);

        repository.update(user);
    }

    @Transactional
    public void deductBalance(String userId, BigDecimal amount) {
        User user = findUserById(userId);
        user.deductBalance(amount);

        repository.update(user);
    }

    private User findUserById(String userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
    }

}
