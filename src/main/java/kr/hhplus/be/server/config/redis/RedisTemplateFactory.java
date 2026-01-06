package kr.hhplus.be.server.config.redis;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RedisTemplateFactory {

    private final Map<RedisDatabaseType, RedisTemplate<String, ?>> templateMap;

    public RedisTemplateFactory(List<TypedRedisTemplateProvider> providers) {
        this.templateMap = providers.stream()
                .collect(Collectors.toMap(
                        TypedRedisTemplateProvider::getType,
                        TypedRedisTemplateProvider::getTemplate
                ));
    }

    @SuppressWarnings("unchecked")
    public RedisTemplate<String, Object> getDefaultTemplate() {
        return (RedisTemplate<String, Object>) templateMap.get(RedisDatabaseType.DEFAULT);
    }

    public StringRedisTemplate getQueueTemplate() {
        return (StringRedisTemplate) templateMap.get(RedisDatabaseType.QUEUE);
    }

    // ===========================================
    // 기본 DB Operations (캐시, 세션 등)
    // ===========================================

    public ValueOperations<String, Object> opsForValue() {
        return getDefaultTemplate().opsForValue();
    }

    public HashOperations<String, String, Object> opsForHash() {
        return getDefaultTemplate().opsForHash();
    }

    public ListOperations<String, Object> opsForList() {
        return getDefaultTemplate().opsForList();
    }

    public SetOperations<String, Object> opsForSet() {
        return getDefaultTemplate().opsForSet();
    }

    public ZSetOperations<String, Object> opsForZSet() {
        return getDefaultTemplate().opsForZSet();
    }

    // ===========================================
    // 대기열 DB Operations (Sorted Set 최적화)
    // ===========================================

    public ZSetOperations<String, String> queueOpsForZSet() {
        return getQueueTemplate().opsForZSet();
    }

    public ValueOperations<String, String> queueOpsForValue() {
        return getQueueTemplate().opsForValue();
    }

    public SetOperations<String, String> queueOpsForSet() {
        return getQueueTemplate().opsForSet();
    }

    // ===========================================
    // 키 관리 (TTL 포함)
    // ===========================================

    public Boolean delete(String key) {
        return getDefaultTemplate().delete(key);
    }

    public Boolean deleteQueueKey(String key) {
        return getQueueTemplate().delete(key);
    }

    public Boolean hasKey(String key) {
        return getDefaultTemplate().hasKey(key);
    }

    public Boolean hasQueueKey(String key) {
        return getQueueTemplate().hasKey(key);
    }

    public Boolean expire(String key, Duration duration) {
        return getDefaultTemplate().expire(key, duration);
    }

    public Boolean expireQueueKey(String key, Duration duration) {
        return getQueueTemplate().expire(key, duration);
    }
}
