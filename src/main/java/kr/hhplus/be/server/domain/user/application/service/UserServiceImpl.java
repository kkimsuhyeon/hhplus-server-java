package kr.hhplus.be.server.domain.user.application.service;

import kr.hhplus.be.server.domain.user.model.entity.UserEntity;
import kr.hhplus.be.server.domain.user.model.repository.UserRepository;
import kr.hhplus.be.server.domain.user.model.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUser(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않음"));
    }

    @Override
    @Transactional
    public void create(UserEntity user) {
        repository.save(user);
    }
}
