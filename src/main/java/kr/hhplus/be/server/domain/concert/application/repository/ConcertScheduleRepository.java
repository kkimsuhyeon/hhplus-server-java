package kr.hhplus.be.server.domain.concert.application.repository;

import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;

import java.util.List;

/**
 * ConcertSchedule Repository 인터페이스
 * 순수 도메인 모델(ConcertSchedule)만 다루며, JPA에 대해 전혀 알지 못함
 */
public interface ConcertScheduleRepository {

    /**
     * ID로 콘서트 스케줄 조회
     */
    ConcertSchedule findById(String id);

    /**
     * 콘서트 ID로 스케줄 목록 조회
     */
    List<ConcertSchedule> findByConcertId(String concertId);

    /**
     * 콘서트 스케줄 저장 (생성 및 수정)
     */
    ConcertSchedule save(ConcertSchedule schedule);
}
