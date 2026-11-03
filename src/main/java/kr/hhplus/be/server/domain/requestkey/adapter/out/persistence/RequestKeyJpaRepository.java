package kr.hhplus.be.server.domain.requestkey.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequestKeyJpaRepository extends JpaRepository<RequestKeyEntity, String> {
    Optional<RequestKeyEntity> findByUserIdAndIdempotencyKey(String userId, String idempotencyKey);
}
