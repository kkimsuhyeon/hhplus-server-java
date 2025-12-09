package kr.hhplus.be.server.domain.reservation.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, String>, JpaSpecificationExecutor<ReservationEntity> {

}
