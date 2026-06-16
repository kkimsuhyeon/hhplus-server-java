package kr.hhplus.be.server.domain.user.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.user.application.dto.command.CreateUserCommand;
import kr.hhplus.be.server.domain.user.exception.UserErrorCode;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.domain.user.port.UserRepository;
import kr.hhplus.be.server.domain.user.service.UserRegistration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandService {

    private final UserRegistration userRegistration;
    private final UserRepository repository;

    public User create(CreateUserCommand command) {
        User user = userRegistration.register(command.getEmail(), command.getPassword(), command.getName());
        return repository.save(user);
    }

    public void changeName(String userId, String name){
        User user = repository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));

        user.changeName(name);
        repository.update(user);
    }

    public void addBalance(String userId, BigDecimal amount) {
        User user = repository.findByIdForUpdate(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));

        user.addBalance(amount);
        repository.update(user);
    }

    public void deductBalance(String userId, BigDecimal amount) {
        User user = repository.findByIdForUpdate(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));

        user.deductBalance(amount);
        repository.update(user);
    }

    public User save(User user) {
        return repository.save(user);
    }

    public User update(User user) {
        return repository.update(user);
    }
}
