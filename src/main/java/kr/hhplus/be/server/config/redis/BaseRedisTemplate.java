package kr.hhplus.be.server.config.redis;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import java.time.Duration;

public abstract class BaseRedisTemplate {

    private final RedisProperties redisProperties;

    protected BaseRedisTemplate(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    protected RedisConnectionFactory createConnectionFactory(RedisDatabaseType type) {
        // Redis 서버 설정
        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration();
        serverConfig.setHostName(redisProperties.getHost());
        serverConfig.setPort(redisProperties.getPort());
        serverConfig.setDatabase(type.getDatabase());

        if (StringUtils.hasText(redisProperties.getPassword())) {
            serverConfig.setPassword(redisProperties.getPassword());
        }

        GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(redisProperties.getMaxActive());
        poolConfig.setMaxIdle(redisProperties.getMaxIdle());
        poolConfig.setMinIdle(redisProperties.getMinIdle());
        poolConfig.setMaxWait(Duration.ofMillis(redisProperties.getMaxWait()));

        // 풀 검증 설정
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);


        // Socket 옵션 설정
        SocketOptions socketOptions = SocketOptions.builder()
                .connectTimeout(Duration.ofMillis(redisProperties.getConnectTimeout()))
                .build();

        // Client 옵션 설정
        ClientOptions clientOptions = ClientOptions.builder()
                .socketOptions(socketOptions)
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .build();

        // Lettuce 클라이언트 설정
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig)
                .clientOptions(clientOptions)
                .commandTimeout(Duration.ofMillis(redisProperties.getTimeout()))
                .build();

        LettuceConnectionFactory factory = new LettuceConnectionFactory(serverConfig, clientConfig);
        factory.afterPropertiesSet();
        return factory;
    }

    protected void setObjectSerializer(RedisTemplate<String, Object> template) {
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
    }

    protected void setStringSerializer(RedisTemplate<String, String> template) {
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
    }


}
