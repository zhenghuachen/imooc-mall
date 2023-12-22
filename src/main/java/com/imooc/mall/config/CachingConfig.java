package com.imooc.mall.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

/**
 * 描述： 缓存配置类
 */
@Configuration // 表示该类或接口是一个 Spring 配置类
@EnableCaching // 用于启用 Spring 缓存功能
public class CachingConfig {
    @Bean //注解用于标记一个方法，表示该方法返回一个Bean对象。此处返回了一个RedisCacheManager类型的Bean对象。
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        // 创建一个RedisCacheWriter对象，用于将数据写入Redis缓存
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.lockingRedisCacheWriter(connectionFactory);
        // 创建一个RedisCacheConfiguration对象，用于配置缓存的基本属性，如过期时间等。
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        // 用于设置缓存过期时间为30秒
        cacheConfiguration = cacheConfiguration.entryTtl(Duration.ofSeconds(30));

        // 用于创建一个RedisCacheManager对象，并将RedisCacheWriter和RedisCacheConfiguration对象作为参数传递
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisCacheWriter, cacheConfiguration);
        return redisCacheManager;
    }
}
