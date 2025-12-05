package kr.hhplus.be.server.domain.token.adapter.out.memory;

import kr.hhplus.be.server.domain.token.application.QueueTokenRepository;
import kr.hhplus.be.server.domain.token.model.QueueToken;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
@RequiredArgsConstructor
public class QueueTokenMemoryRepositoryAdapter implements QueueTokenRepository {

    private final QueueTokenMemoryRepository tokenRepository;

    @Override
    public Optional<QueueToken> findById(String id) {
        return tokenRepository.findById(id);
    }

    @Override
    public Optional<QueueToken> findByUserId(String userId) {
        return tokenRepository.findByUserId(userId);
    }

    @Override
    public List<QueueToken> findWaitingTokens(int limit) {
        return tokenRepository.findAll().stream()
                .filter(QueueToken::isWaiting)
                .toList();
    }

    @Override
    public int getRank(String id) {
        return 0;
    }

    @Override
    public int countWaitingTokens() {
        return tokenRepository.getLastpositionInQueue();
    }

    @Override
    public void deleteExpiredTokens() {

    }


    @Override
    public QueueToken save(QueueToken queueToken) {
        return null;
    }
}
