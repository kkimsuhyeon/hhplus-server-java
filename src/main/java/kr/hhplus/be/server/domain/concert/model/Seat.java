package kr.hhplus.be.server.domain.concert.model;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.domain.concert.exception.SeatErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class Seat {

    private String id;

    private SeatStatus status;

    private BigDecimal price;

    private String scheduleId;

    public boolean isReservable() {
        return this.status == SeatStatus.AVAILABLE;
    }

    public void reserve() {
        if (!isReservable()) {
            throw new BusinessException(SeatErrorCode.ALREADY_RESERVED);
        }

        this.status = SeatStatus.RESERVING;
    }

    public void confirmReservation() {
        if (this.status != SeatStatus.RESERVING) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT);
        }

        this.status = SeatStatus.RESERVED;
    }
}
