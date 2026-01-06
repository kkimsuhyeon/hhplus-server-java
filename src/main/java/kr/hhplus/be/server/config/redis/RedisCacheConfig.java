package kr.hhplus.be.server.config.redis;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    private final DefaultRedisTemplateProvider defaultRedisTemplateProvider;

    public RedisCacheConfig(DefaultRedisTemplateProvider defaultRedisTemplateProvider) {
        this.defaultRedisTemplateProvider = defaultRedisTemplateProvider;
    }

    @Bean
    public CacheManager cacheManager() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(serializer))
                .entryTtl(Duration.ofMinutes(30))
                .disableCachingNullValues();

        // 캐시별 TTL 설정
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("concerts", defaultConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigurations.put("concertSchedules", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("seats", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("users", defaultConfig.entryTtl(Duration.ofHours(1)));

        return RedisCacheManager.builder(defaultRedisTemplateProvider.getTemplate().getConnectionFactory())
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}
