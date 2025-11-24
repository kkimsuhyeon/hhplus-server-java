package kr.hhplus.be.server.domain.concert.adapter.persistence;

import kr.hhplus.be.server.domain.concert.model.entity.ConcertScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConcertScheduleJpaRepository extends JpaRepository<ConcertScheduleEntity, String>, JpaSpecificationExecutor<ConcertScheduleEntity> {

    /**
     * 콘서트 ID로 스케줄 목록 조회
     */
    @Query("SELECT cs FROM ConcertScheduleEntity cs WHERE cs.concert.id = :concertId")
    List<ConcertScheduleEntity> findByConcertId(@Param("concertId") String concertId);
}
