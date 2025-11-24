package kr.hhplus.be.server.domain.reservation.model;

/**
 * 예약 상태
 */
public enum ReservationStatus {
    PENDING_PAYMENT,  // 결제 대기
    CONFIRMED,        // 확정
    CANCELLED,        // 취소
}
