package kr.hhplus.be.server.domain.requestkey.application.service;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.requestkey.exception.RequestKeyErrorCode;
import kr.hhplus.be.server.domain.requestkey.model.RequestKey;
import kr.hhplus.be.server.domain.requestkey.port.RequestKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestKeyCommandService {

    private final RequestKeyRepository requestKeyRepository;

    public boolean isRetry(String userId, UUID idempotencyKey, RequestKey requestKey) {
        Optional<RequestKey> stored = requestKeyRepository.findRequest(userId, idempotencyKey);

        if (stored.isEmpty()) {
            return false;
        }

        if (!stored.get().isSameRequest(requestKey)) {
            throw new BusinessException(RequestKeyErrorCode.KEY_REUSED);
        }

        return true;
    }

    public void save(String userId, UUID idempotencyKey, RequestKey requestKey) {
        requestKeyRepository.save(userId, idempotencyKey, requestKey);
    }
}
