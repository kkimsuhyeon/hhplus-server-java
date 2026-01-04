package kr.hhplus.be.server.domain.token.adapter.out.memory;

import kr.hhplus.be.server.domain.token.application.QueueTokenRepository;
import kr.hhplus.be.server.domain.token.model.QueueToken;
import kr.hhplus.be.server.domain.token.model.TokenStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Primary
@Repository
@RequiredArgsConstructor
public class QueueTokenMemoryRepositoryAdapter implements QueueTokenRepository {

    private final QueueTokenMemoryRepository tokenRepository;

    @Override
    public List<QueueToken> findAll() {
        return tokenRepository.findAll();
    }

    @Override
    public Optional<QueueToken> findById(String id) {
        return tokenRepository.findById(id);
    }

    @Override
    public Optional<QueueToken> findByUserId(String userId) {
        return tokenRepository.findByUserId(userId);
    }

    @Override
    public List<QueueToken> findByStatus(TokenStatus status, int limit) {
        return tokenRepository.findByStatus(status, limit);
    }

    @Override
    public List<QueueToken> findByExpiredAtBefore(LocalDateTime referenceTime) {
        return tokenRepository.findByExpiredAtBefore(referenceTime);
    }

    @Override
    public int countByStatus(TokenStatus status) {
        return tokenRepository.countByStatus(status);
    }

    @Override
    public int getRank(String id) {
        return tokenRepository.getRank(id);
    }

    @Override
    public QueueToken save(QueueToken queueToken) {
        return tokenRepository.save(queueToken.getId(), queueToken);
    }

    @Override
    public void delete(String id) {
        tokenRepository.delete(id);
    }
}
