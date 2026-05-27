package kr.hhplus.be.server.domain.user.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.user.application.UserRepository;
import kr.hhplus.be.server.domain.user.application.assembler.UserAssembler;
import kr.hhplus.be.server.domain.user.application.dto.command.CreateUserCommand;
import kr.hhplus.be.server.domain.user.exception.UserErrorCode;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final UserRepository repository;

    @Transactional
    public User create(CreateUserCommand command) {
        User user = UserAssembler.toModel(command);
        return repository.save(user);
    }

    @Transactional
    public void addBalance(String userId, BigDecimal amount) {
        User user = repository.findByIdWithLock(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));

        user.addBalance(amount);
        repository.update(user);
    }

    @Transactional
    public void deductBalance(String userId, BigDecimal amount) {
        User user = repository.findByIdWithLock(userId)
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
