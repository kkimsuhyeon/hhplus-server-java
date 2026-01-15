package kr.hhplus.be.server.domain.auth.exception;

import kr.hhplus.be.server.config.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    NO_EXISTS_USER(HttpStatus.BAD_REQUEST, "ATH001", "유저가 존재하지 않습니다"),
    INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST, "ATH002", "비밀번호가 일치하지 않습니다"),
    EXISTS_EMAIL(HttpStatus.BAD_REQUEST, "ATH003", "이미 존재하는 이메일입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "ATH004", "토큰이 유효하지 않습니다"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
