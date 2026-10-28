package kr.hhplus.be.server.domain.requestkey.application.service;

import kr.hhplus.be.server.domain.requestkey.model.RequestKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestKeyCommandService {

    public boolean isRetry(String userId, UUID idempotencyKey, RequestKey requestKey) {
        throw new UnsupportedOperationException();
    }

    public void save(String userId, UUID idempotencyKey, RequestKey requestKey) {
        throw new UnsupportedOperationException();
    }
}
