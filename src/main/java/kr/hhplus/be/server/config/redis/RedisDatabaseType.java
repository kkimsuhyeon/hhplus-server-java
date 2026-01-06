package kr.hhplus.be.server.config.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisDatabaseType {

    DEFAULT(0),
    QUEUE(1),
    ;

    private final int database;
}
