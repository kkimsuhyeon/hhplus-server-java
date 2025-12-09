package kr.hhplus.be.server.config.exception.exceptions;

import lombok.Getter;

@Getter
public class ServerErrorException extends BusinessException {

    private final String description;

    public ServerErrorException(String description) {
        super(CommonErrorCode.SERVER_ERROR);
        this.description = description;
    }
}
