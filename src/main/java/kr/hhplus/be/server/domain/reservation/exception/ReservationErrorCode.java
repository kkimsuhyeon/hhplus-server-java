package kr.hhplus.be.server.domain.reservation.exception;

import kr.hhplus.be.server.config.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReservationErrorCode implements ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "RSV001", "예약이 존재하지 않습니다"),
    EXPIRED(HttpStatus.BAD_REQUEST, "RSV002", "예약이 만료되었습니다"),
    ALREADY_PAID(HttpStatus.BAD_REQUEST, "RSV003", "이미 결제된 예약입니다"),
    CANCELED(HttpStatus.BAD_REQUEST, "RSV004", "취소된 예약입니다"),
    NOT_PAYABLE(HttpStatus.BAD_REQUEST, "RSV005", "결제 가능한 상태가 아닙니다"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

}
