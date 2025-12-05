package kr.hhplus.be.server.domain.token.adapter.out.redis;

import kr.hhplus.be.server.domain.token.application.QueueTokenRepository;
import kr.hhplus.be.server.domain.token.model.QueueToken;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class QueueTokenRedisRepositoryAdapter implements QueueTokenRepository {

    @Override
    public Optional<QueueToken> findById(String id) {
        return Optional.empty();
    }

    @Override
    public Optional<QueueToken> findByUserId(String userId) {
        return Optional.empty();
    }

    @Override
    public List<QueueToken> findWaitingTokens(int limit) {
        return List.of();
    }

    @Override
    public int countWaitingTokens() {
        return 0;
    }

    @Override
    public void deleteExpiredTokens() {

    }

    @Override
    public int getRank(String id) {
        return 0;
    }

    @Override
    public QueueToken save(QueueToken queueToken) {
        return null;
    }
}
