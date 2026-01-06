package kr.hhplus.be.server.config.redis;

import org.springframework.data.redis.core.RedisTemplate;

public interface TypedRedisTemplateProvider {

    RedisDatabaseType getType();

    RedisTemplate<String, ?> getTemplate();
}
