package kr.hhplus.be.server.domain.concert.application;

import kr.hhplus.be.server.domain.concert.model.Seat;

import java.util.List;

/**
 * Seat Repository 인터페이스
 * 순수 도메인 모델(Seat)만 다루며, JPA에 대해 전혀 알지 못함
 */
public interface SeatRepository {
    
    /**
     * ID로 좌석 조회
     */
    Seat findById(String id);

    /**
     * ID로 좌석 조회 (비관적 락)
     */
    Seat findByIdForUpdate(String id);

    /**
     * 스케줄 ID로 좌석 목록 조회
     */
    List<Seat> findByScheduleId(String scheduleId);
    
    /**
     * 좌석 저장 (생성 및 수정)
     */
    Seat save(Seat seat);
}
