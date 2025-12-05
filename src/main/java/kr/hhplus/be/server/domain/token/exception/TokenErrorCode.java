package kr.hhplus.be.server.domain.token.exception;

import kr.hhplus.be.server.config.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TokenErrorCode implements ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "TKN001", "토큰을 찾을 수 없습니다."),
    ALREADY_EXPIRED(HttpStatus.BAD_REQUEST, "TKN002", "토큰이 만료되었습니다."),
    NOT_ACTIVE(HttpStatus.BAD_REQUEST, "TKN003", "토큰이 활성화 상태가 아닙니다"),
    INVALID(HttpStatus.BAD_REQUEST, "TKN004", "유효하지 않은 토큰입니다"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
