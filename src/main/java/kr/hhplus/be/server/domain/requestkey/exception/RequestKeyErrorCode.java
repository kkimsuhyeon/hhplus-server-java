package kr.hhplus.be.server.domain.requestkey.exception;

import kr.hhplus.be.server.config.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RequestKeyErrorCode implements ErrorCode {

    KEY_REUSED(HttpStatus.CONFLICT, "RQK001", "이미 다른 요청에 사용된 멱등 키 입니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

}
