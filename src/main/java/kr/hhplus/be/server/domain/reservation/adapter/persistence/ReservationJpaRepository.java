package kr.hhplus.be.server.domain.reservation.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA Repository for ReservationJpaEntity
 */
public interface ReservationJpaRepository extends JpaRepository<ReservationJpaEntity, String> {
}
