package kr.hhplus.be.server.domain.token.application;

import kr.hhplus.be.server.domain.token.model.QueueToken;
import kr.hhplus.be.server.domain.token.model.TokenStatus;

import java.util.List;
import java.util.Optional;

public interface QueueTokenRepository {

    Optional<QueueToken> findById(String id);

    Optional<QueueToken> findByUserId(String userId);

    List<QueueToken> findByStatus(TokenStatus status, int limit);

    List<QueueToken> findAll();

    int countByStatus(TokenStatus status);

    int getRank(String id);

    QueueToken save(QueueToken queueToken);

    void delete(String id);
}
