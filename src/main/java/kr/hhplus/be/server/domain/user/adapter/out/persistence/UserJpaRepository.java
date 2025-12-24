package kr.hhplus.be.server.domain.user.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, String>, JpaSpecificationExecutor<UserEntity> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u from UserEntity u where u.id = :userId")
    Optional<UserEntity> findByIdForUpdate(String userId);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
