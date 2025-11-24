package kr.hhplus.be.server.domain.concert.exception;

import kr.hhplus.be.server.config.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SeatErrorCode implements ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "ST001", "좌석이 존재하지 않습니다"),
    ALREADY_RESERVED(HttpStatus.BAD_REQUEST, "ST002", "이미 예약된 좌석입니다"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
