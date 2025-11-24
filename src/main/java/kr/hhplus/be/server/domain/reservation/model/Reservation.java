package kr.hhplus.be.server.domain.reservation.model;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.config.exception.exceptions.ServerErrorException;
import kr.hhplus.be.server.domain.reservation.exception.ReservationErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 순수 도메인 모델 - Reservation
 * JPA에 대해 전혀 알지 못하며, 오직 예약 도메인의 비즈니스 규칙만 표현
 */
@Getter
@Builder
@AllArgsConstructor
public class Reservation {
    
    private String id;
    private ReservationStatus status;
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 연관 Entity 참조 대신 ID만 보유
    private String userId;
    private String seatId;
    
    // 예약 당시의 좌석 가격 (스냅샷)
    private BigInteger seatPrice;
    
    // ========== 비즈니스 로직 ==========
    
    /**
     * 결제 완료 처리
     */
    public void completePayment() {
        if (!isPayable()) {
            throw new BusinessException(ReservationErrorCode.NOT_PAYABLE);
        }
        this.status = ReservationStatus.CONFIRMED;
    }
    
    /**
     * 결제 검증
     */
    public void validateForPayment(String userId) {
        if (!isOwnedBy(userId)) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN_ERROR);
        }
        if (isExpired()) {
            throw new BusinessException(ReservationErrorCode.EXPIRED);
        }
        if (!isPayable()) {
            if (this.status == ReservationStatus.CONFIRMED) {
                throw new BusinessException(ReservationErrorCode.ALREADY_PAID);
            }
            if (this.status == ReservationStatus.CANCELLED) {
                throw new BusinessException(ReservationErrorCode.CANCELED);
            }
            throw new ServerErrorException("validateForPayment : 이상한 에러");
        }
    }
    
    /**
     * 예약 만료 여부 확인
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }
    
    /**
     * 결제 가능 여부 확인
     */
    public boolean isPayable() {
        return this.status == ReservationStatus.PENDING_PAYMENT;
    }
    
    /**
     * 소유자 확인
     */
    public boolean isOwnedBy(String userId) {
        return this.userId.equals(userId);
    }
    
    /**
     * 새 예약 생성 팩토리 메서드
     */
    public static Reservation create(String userId, String seatId, BigInteger seatPrice) {
        return Reservation.builder()
                .status(ReservationStatus.PENDING_PAYMENT)
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .userId(userId)
                .seatId(seatId)
                .seatPrice(seatPrice)
                .build();
    }
}
