package kr.hhplus.be.server.domain.requestkey.port;

import kr.hhplus.be.server.domain.requestkey.model.RequestKey;

import java.util.Optional;
import java.util.UUID;

public interface RequestKeyRepository {

    Optional<RequestKey> findRequest(String userId, UUID idempotencyKey);

    void save(String userId, UUID idempotencyKey, RequestKey requestKey);

}
