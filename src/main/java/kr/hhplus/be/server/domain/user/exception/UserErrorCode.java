package kr.hhplus.be.server.domain.user.exception;

import kr.hhplus.be.server.config.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "USR001", "해당 유저가 존재하지 않습니다"),
    NOT_ENOUGH_POINT(HttpStatus.BAD_REQUEST, "USR002", "잔액이 부족합니다"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
