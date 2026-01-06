package kr.hhplus.be.server.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {
    private String host;
    private int port;
    private String password;

    // 커넥션 풀 설정
    private int maxActive = 8;
    private int maxIdle = 8;
    private int minIdle = 2;
    private long maxWait = 3000;

    // 타임아웃 설정
    private long timeout = 3000;
    private long connectTimeout = 3000;
}
