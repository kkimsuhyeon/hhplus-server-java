package kr.hhplus.be.server.domain.user.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, String>, JpaSpecificationExecutor<UserJpaEntity> {
}
