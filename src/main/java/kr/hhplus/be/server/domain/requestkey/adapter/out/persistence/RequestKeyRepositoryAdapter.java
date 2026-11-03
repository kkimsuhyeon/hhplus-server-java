package kr.hhplus.be.server.domain.requestkey.adapter.out.persistence;

import kr.hhplus.be.server.domain.requestkey.model.RequestKey;
import kr.hhplus.be.server.domain.requestkey.port.RequestKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RequestKeyRepositoryAdapter implements RequestKeyRepository {

    private final RequestKeyJpaRepository jpaRepository;

    @Override
    public Optional<RequestKey> findRequest(String userId, UUID idempotencyKey) {
        return jpaRepository.findByUserIdAndIdempotencyKey(userId, idempotencyKey.toString())
                .map(RequestKeyEntity::toModel);
    }

    @Override
    public void save(String userId, UUID idempotencyKey, RequestKey requestKey) {
        jpaRepository.save(RequestKeyEntity.create(userId, idempotencyKey, requestKey));
    }
}
