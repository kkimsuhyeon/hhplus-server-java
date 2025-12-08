package kr.hhplus.be.server.domain.token.application;

import kr.hhplus.be.server.domain.token.model.QueueToken;

import java.util.List;
import java.util.Optional;

public interface QueueTokenRepository {

    Optional<QueueToken> findById(String id);

    Optional<QueueToken> findByUserId(String userId);

    List<QueueToken> findWaitingTokens(int limit);

    int countWaitingTokens();

    void deleteExpiredTokens();

    int getRank(String id);

    QueueToken save(QueueToken queueToken);

    void delete(String id);
}
