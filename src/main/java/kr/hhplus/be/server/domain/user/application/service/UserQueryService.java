package kr.hhplus.be.server.domain.user.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.user.application.dto.query.FindUserQuery;
import kr.hhplus.be.server.domain.user.port.UserCriteria;
import kr.hhplus.be.server.domain.user.port.UserRepository;
import kr.hhplus.be.server.domain.user.exception.UserErrorCode;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository repository;

    @Transactional(readOnly = true)
    public Page<User> getUsers(FindUserQuery query, Pageable pageable) {
        UserCriteria criteria = query == null ? UserCriteria.empty() : query.toCriteria();

        return repository.findAllByCriteria(criteria, pageable);
    }

    @Transactional(readOnly = true)
    public User getUser(String userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
    }
}
