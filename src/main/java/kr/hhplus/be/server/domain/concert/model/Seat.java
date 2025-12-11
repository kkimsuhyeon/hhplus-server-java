package kr.hhplus.be.server.domain.concert.model;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.domain.concert.exception.SeatErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class Seat {

    private String id;

    private SeatStatus status;

    private BigDecimal price;

    private String scheduleId;

    private String userId;

    private LocalDateTime holdExpiresAt;

    public boolean isReservable() {
        return this.status == SeatStatus.AVAILABLE;
    }

    public void reserve(String userId) {
        if (!isReservable()) {
            throw new BusinessException(SeatErrorCode.ALREADY_RESERVED);
        }

        this.status = SeatStatus.RESERVING;
        this.userId = userId;
        this.holdExpiresAt = LocalDateTime.now().plusMinutes(5);
    }

    public void confirm() {
        if (this.status != SeatStatus.RESERVING) {
            throw new BusinessException(SeatErrorCode.INVALID_STATUS);
        }

        this.status = SeatStatus.RESERVED;
        this.holdExpiresAt = null;
    }

    public void release() {
        this.status = SeatStatus.AVAILABLE;
        this.userId = null;
        this.holdExpiresAt = null;
    }

    public boolean isOwnerBy(String userId) {
        return userId.equals(this.userId);
    }

    public boolean isHoldExpired() {
        return holdExpiresAt != null && LocalDateTime.now().isAfter(holdExpiresAt);
    }

}