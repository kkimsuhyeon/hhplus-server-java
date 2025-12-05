package kr.hhplus.be.server.domain.token.application;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.token.exception.TokenErrorCode;
import kr.hhplus.be.server.domain.token.model.QueueToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueueTokenService {

    private final QueueTokenRepository queueTokenRepository;

    public QueueToken issueToken(String userId) {
        QueueToken token = QueueToken.create(userId);
        return queueTokenRepository.save(token);
    }

    public QueueToken getToken(String id) {
        return queueTokenRepository.findById(id)
                .orElseThrow(() -> new BusinessException(TokenErrorCode.NOT_FOUND));
    }
}
