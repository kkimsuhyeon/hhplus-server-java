package kr.hhplus.be.server.domain.token.application;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.token.exception.TokenErrorCode;
import kr.hhplus.be.server.domain.token.model.QueueToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueueTokenService {

    private final QueueTokenRepository queueTokenRepository;

    @Transactional
    public QueueToken issueToken(String userId) {
        QueueToken result = queueTokenRepository.findByUserId(userId).orElseGet(() -> {
            QueueToken token = QueueToken.create(userId);
            return queueTokenRepository.save(token);
        });

        if (result.isExpired()) {
            QueueToken token = QueueToken.create(userId);
            queueTokenRepository.delete(result.getId());
            return queueTokenRepository.save(token);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public QueueToken getToken(String id) {
        QueueToken token = queueTokenRepository.findById(id)
                .orElseThrow(() -> new BusinessException(TokenErrorCode.NOT_FOUND));

        if (token.isExpired()) {
            throw new BusinessException(TokenErrorCode.ALREADY_EXPIRED);
        }

        return token;
    }

    public int getRank(String id) {
        return queueTokenRepository.getRank(id);
    }

    public void deleteExpiredTokens() {
        queueTokenRepository.deleteExpiredTokens();
    }
}
