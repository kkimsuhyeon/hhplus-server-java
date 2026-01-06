package kr.hhplus.be.server.config.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class QueueRedisTemplateProvider extends BaseRedisTemplate implements TypedRedisTemplateProvider {

    private final RedisDatabaseType TYPE = RedisDatabaseType.QUEUE;
    private final StringRedisTemplate template;

    public QueueRedisTemplateProvider(RedisProperties redisProperties) {
        super(redisProperties);
        this.template = new StringRedisTemplate();
        this.template.setConnectionFactory(createConnectionFactory(TYPE));
        setStringSerializer(this.template);
        this.template.afterPropertiesSet();
    }

    @Override
    public RedisDatabaseType getType() {
        return TYPE;
    }

    @Override
    public RedisTemplate<String, ?> getTemplate() {
        return template;
    }
}
