package kr.hhplus.be.server.domain.user.application;

import kr.hhplus.be.server.domain.user.application.dto.UserCriteria;
import kr.hhplus.be.server.domain.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepository {

    Page<User> findAllByCriteria(UserCriteria criteria, Pageable pageable);

    Optional<User> findById(String id);

    User save(User user);
}
