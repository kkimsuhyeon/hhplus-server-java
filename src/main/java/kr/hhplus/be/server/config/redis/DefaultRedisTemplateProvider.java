package kr.hhplus.be.server.config.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class DefaultRedisTemplateProvider extends BaseRedisTemplate implements TypedRedisTemplateProvider {

    private static final RedisDatabaseType TYPE = RedisDatabaseType.DEFAULT;
    private final RedisTemplate<String, Object> template;

    public DefaultRedisTemplateProvider(RedisProperties redisProperties) {
        super(redisProperties);
        this.template = new RedisTemplate<>();
        this.template.setConnectionFactory(createConnectionFactory(TYPE));
        setObjectSerializer(this.template);
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
