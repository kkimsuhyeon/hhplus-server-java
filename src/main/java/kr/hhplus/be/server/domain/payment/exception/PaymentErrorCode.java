package kr.hhplus.be.server.domain.payment.exception;

import kr.hhplus.be.server.config.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "PAY001", "해당 결제 정보가 존재하지 않습니다"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
