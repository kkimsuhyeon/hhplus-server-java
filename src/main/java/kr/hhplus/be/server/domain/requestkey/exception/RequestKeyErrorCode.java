package kr.hhplus.be.server.domain.requestkey.exception;

import kr.hhplus.be.server.config.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RequestKeyErrorCode implements ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "RQK001", "임시에러"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

}
