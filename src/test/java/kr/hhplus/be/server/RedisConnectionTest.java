package kr.hhplus.be.server;

import kr.hhplus.be.server.config.redis.RedisTemplateFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RedisConnectionTest {

    @Autowired
    private RedisTemplateFactory redisTemplateFactory;

    @Test
    void redis_연결_테스트() {
        redisTemplateFactory.queueOpsForValue().set("test:123", "hello");
        String result = redisTemplateFactory.queueOpsForValue().get("test:123");

        assertThat(result).isEqualTo("hello");
    }
}
