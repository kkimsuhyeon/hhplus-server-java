package kr.hhplus.be.server.domain.concert.exception;

import kr.hhplus.be.server.config.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ConcertScheduleErrorCode implements ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "SCD001", "스케쥴을 찾을 수 없습니다"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
