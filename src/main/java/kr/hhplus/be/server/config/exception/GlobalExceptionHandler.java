package kr.hhplus.be.server.config.exception;

import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.config.exception.exceptions.ValidationError;
import kr.hhplus.be.server.shared.dto.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<?>> handleBusinessException(BusinessException exception) {
        log.error("BUSINESS ERROR: ", exception);
        return ResponseEntity.status(exception.getErrorCode().getStatus())
                .body(BaseResponse.fail(exception.getErrorCode()));
    }


    @Override
    protected @Nullable ResponseEntity<Object> handleServletRequestBindingException(
            ServletRequestBindingException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        log.debug("Request binding failed: {}", exception.getMessage());

        return handleExceptionInternal(
                exception,
                BaseResponse.fail(CommonErrorCode.INVALID_INPUT),
                headers,
                status,
                request
        );
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        log.debug("Request validation failed: {}", exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getCode())
                .toList());

        List<ValidationError> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(ValidationError::new)
                .toList();

        return handleExceptionInternal(
                exception,
                BaseResponse.fail(CommonErrorCode.INVALID_INPUT, errors),
                headers,
                status,
                request
        );
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        log.debug("Type mismatch: {} -> {}", exception.getPropertyName(), exception.getRequiredType());

        return handleExceptionInternal(
                exception,
                BaseResponse.fail(CommonErrorCode.INVALID_INPUT),
                headers,
                status,
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleException(Exception exception) {
        log.error("UNKNOWN ERROR: ", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.fail(CommonErrorCode.SERVER_ERROR));
    }
}
