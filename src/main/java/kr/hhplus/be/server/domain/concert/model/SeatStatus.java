package kr.hhplus.be.server.domain.concert.model;

/**
 * 좌석 상태
 */
public enum SeatStatus {
    AVAILABLE,   // 예약 가능
    RESERVING,   // 예약 중
    RESERVED,    // 예약 완료
}
