package kr.hhplus.be.server.domain.user.application;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.user.application.dto.UserCriteria;
import kr.hhplus.be.server.domain.user.application.dto.command.CreateUserCommand;
import kr.hhplus.be.server.domain.user.application.dto.query.FindUserQuery;
import kr.hhplus.be.server.domain.user.exception.UserErrorCode;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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
        return repository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
    }

    @Transactional
    public User create(CreateUserCommand command) {
        User user = UserMapper.toModel(command);
        return repository.save(user);
    }

    @Transactional
    public void addBalance(String userId, BigDecimal amount) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));

        user.addBalance(amount);
        repository.update(user);
    }

    @Transactional
    public void deductBalance(String userId, BigDecimal amount) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));

        user.deductBalance(amount);
        repository.update(user);
    }

    @Transactional
    public User save(User user) {
        return repository.save(user);
    }

    @Transactional
    public User update(User user) {
        return repository.update(user);
    }

}
