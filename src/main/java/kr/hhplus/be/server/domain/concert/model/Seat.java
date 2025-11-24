package kr.hhplus.be.server.domain.concert.model;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;

/**
 * 순수 도메인 모델 - Seat
 */
@Getter
@Builder
@AllArgsConstructor
public class Seat {
    
    private String id;
    private int seatNumber;
    private SeatStatus status;
    private BigInteger price;
    private String scheduleId;
    
    /**
     * 예약 가능 여부 확인
     */
    public boolean isReservable() {
        return this.status == SeatStatus.AVAILABLE;
    }
    
    /**
     * 좌석 예약 처리
     */
    public void reserve() {
        if (!isReservable()) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT);
        }
        this.status = SeatStatus.RESERVING;
    }
    
    /**
     * 좌석 예약 확정
     */
    public void confirmReservation() {
        this.status = SeatStatus.RESERVED;
    }
}
