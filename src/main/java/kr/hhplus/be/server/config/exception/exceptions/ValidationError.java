package kr.hhplus.be.server.config.exception.exceptions;

import lombok.Getter;
import org.springframework.validation.FieldError;

@Getter
public class ValidationError {
    private final String field;
    private final String message;

    public ValidationError(FieldError error) {
        this.field = error.getField();
        this.message = error.getDefaultMessage();
    }
}
