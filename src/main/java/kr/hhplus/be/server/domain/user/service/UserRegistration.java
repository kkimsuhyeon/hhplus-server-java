package kr.hhplus.be.server.domain.user.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.user.exception.UserErrorCode;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.domain.user.port.PasswordHasher;
import kr.hhplus.be.server.domain.user.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistration {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public User register(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(UserErrorCode.DUPLICATE_EMAIL);
        }

        return User.create(email, passwordHasher.hash(password));
    }

}
