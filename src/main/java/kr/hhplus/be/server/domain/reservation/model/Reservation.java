package kr.hhplus.be.server.domain.reservation.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.config.exception.exceptions.ServerErrorException;
import kr.hhplus.be.server.domain.reservation.exception.ReservationErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Reservation {

    private String id;

    private ReservationStatus status;

    private BigDecimal paymentAmount;

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String userId;

    private String seatId;

    public void completePayment() {
        if (!isPayable()) {
            throw new BusinessException(ReservationErrorCode.NOT_PAYABLE);
        }

        this.status = ReservationStatus.CONFIRMED;
    }

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

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isPayable() {
        return this.status == ReservationStatus.PENDING_PAYMENT;
    }

    public boolean isOwnedBy(String userId) {
        return this.userId.equals(userId);
    }

    public static Reservation create(String userId, String seatId, BigDecimal paymentAmount) {
        return Reservation.builder()
                .status(ReservationStatus.PENDING_PAYMENT)
                .paymentAmount(paymentAmount)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .userId(userId)
                .seatId(seatId)
                .build();
    }
}
