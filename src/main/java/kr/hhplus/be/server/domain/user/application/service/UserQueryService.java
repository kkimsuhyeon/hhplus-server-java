package kr.hhplus.be.server.domain.user.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.user.application.dto.query.FindUserQuery;
import kr.hhplus.be.server.domain.user.exception.UserErrorCode;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.domain.user.port.UserCriteria;
import kr.hhplus.be.server.domain.user.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository repository;

    public Page<User> getUsers(FindUserQuery query, Pageable pageable) {
        UserCriteria criteria = query == null ? UserCriteria.empty() : query.toCriteria();

        return repository.findAllByCriteria(criteria, pageable);
    }

    public User getUser(String userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
    }
}
