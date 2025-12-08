package kr.hhplus.be.server.domain.token.adapter.out.memory;

import kr.hhplus.be.server.domain.token.model.QueueToken;
import kr.hhplus.be.server.domain.token.model.TokenStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Repository
public class QueueTokenMemoryRepository {

    // 토큰 조회용 Map
    private final Map<String, QueueToken> queueTokens = new ConcurrentHashMap<>();

    // 유저Id, 토큰Id Mapping
    private final Map<String, String> userIdToTokenId = new ConcurrentHashMap<>();

    // 순서 유지 Queue
    private final ConcurrentLinkedQueue<String> waitingQueue = new ConcurrentLinkedQueue<>();

    public List<QueueToken> findAll() {
        return List.copyOf(queueTokens.values());
    }

    public Optional<QueueToken> findById(String id) {
        if (id == null) return Optional.empty();

        return Optional.ofNullable(queueTokens.get(id));
    }

    public Optional<QueueToken> findByUserId(String userId) {
        if (userId == null) return Optional.empty();

        return findById(userIdToTokenId.get(userId));
    }

    public List<QueueToken> findByStatus(TokenStatus status, int limit) {
        return queueTokens.values().stream()
                .filter(token -> token.getStatus() == status)
                .limit(limit)
                .toList();
    }

    public int countByStatus(TokenStatus status) {
        return (int) queueTokens.values().stream()
                .filter(token -> token.getStatus() == status)
                .count();
    }

    public int getRank(String id) {
        int rank = 1;
        for (String tokenId : waitingQueue) {
            if (tokenId.equals(id)) return rank;

            QueueToken token = queueTokens.get(tokenId);
            if (token != null && token.getStatus() == TokenStatus.WAITING) {
                rank++;
            }
        }
        return -1;
    }


    public void delete(String id) {
        QueueToken token = queueTokens.remove(id);

        if (token != null) {
            userIdToTokenId.remove(token.getUserId());
            waitingQueue.remove(id);
        }
    }

    public QueueToken save(String key, QueueToken value) {
        userIdToTokenId.put(value.getUserId(), key);
        waitingQueue.offer(key);
        queueTokens.put(key, value);
        return value;
    }
}
