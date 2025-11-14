package kr.hhplus.be.server.config.exception;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.config.exception.exceptions.ServerErrorException;
import kr.hhplus.be.server.config.exception.exceptions.ValidationError;
import kr.hhplus.be.server.shared.dto.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ServerErrorException.class)
    public ResponseEntity<BaseResponse<?>> handleServerErrorException(ServerErrorException exception) {
        log.error("SERVER ERROR: ", exception);
        return ResponseEntity.status(exception.getErrorCode().getStatus())
                .body(BaseResponse.fail(exception.getErrorCode(), exception.getDescription()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<?>> handleBusinessException(BusinessException exception) {
        log.error("BUSINESS ERROR: ", exception);
        return ResponseEntity.status(exception.getErrorCode().getStatus())
                .body(BaseResponse.fail(exception.getErrorCode()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<BaseResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error("VALIDATION ERROR: ", exception);
        List<ValidationError> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(ValidationError::new)
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.fail(CommonErrorCode.INVALID_INPUT, errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleException(Exception exception) {
        log.error("UNKNOWN ERROR: ", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.fail(CommonErrorCode.SERVER_ERROR, "서버 에러"));
    }
}
