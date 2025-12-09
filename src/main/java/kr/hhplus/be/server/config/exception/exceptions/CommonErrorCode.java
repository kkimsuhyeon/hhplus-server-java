package kr.hhplus.be.server.config.exception.exceptions;

import kr.hhplus.be.server.config.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "입력값이 올바르지 않습니다"),
    UNAUTHORIZED_ERROR(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED_ERROR", "토큰 인증 실패"),
    FORBIDDEN_ERROR(HttpStatus.FORBIDDEN, "FORBIDDEN_ERROR", "접근 거부"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "리소스를 찾을 수 없습니다"),
    UPDATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR", "서버 에러"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR", "서버 에러"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
