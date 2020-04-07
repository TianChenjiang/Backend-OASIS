package com.rubiks.backendoasis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;


@Configuration
public class CacheConfig {

    @Value("${REDIS_HOST:localhost}")
    private String host;

    @Bean(name = "jedis")
    public JedisConnectionFactory redisConnectFactory() {
        return new JedisConnectionFactory(new RedisStandaloneConfiguration(host));
    }

    @Bean
    public CacheManager cacheManager(@Autowired @Qualifier("jedis") RedisConnectionFactory connectionFactory) {
        return RedisCacheManager
                .builder(connectionFactory)
//                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(1))) //1天刷新一次
                .transactionAware()
                .build();
    }
}

