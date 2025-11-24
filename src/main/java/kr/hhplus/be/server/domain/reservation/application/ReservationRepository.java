package kr.hhplus.be.server.domain.reservation.application;

import kr.hhplus.be.server.domain.reservation.model.Reservation;

/**
 * Reservation Repository 인터페이스
 * 순수 도메인 모델(Reservation)만 다루며, JPA에 대해 전혀 알지 못함
 */
public interface ReservationRepository {
    
    /**
     * ID로 예약 조회
     */
    Reservation findById(String id);
    
    /**
     * 예약 저장 (생성 및 수정)
     */
    Reservation save(Reservation reservation);
}
