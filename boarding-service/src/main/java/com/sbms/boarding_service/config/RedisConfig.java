package com.sbms.boarding_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.data.redis.serializer.*;

import java.time.Duration;
import java.util.Random;

@Configuration
@EnableCaching
public class RedisConfig {

    /*
     * ObjectMapper with type information
     */
    @Bean
    public ObjectMapper redisObjectMapper() {

        ObjectMapper mapper = new ObjectMapper();

        mapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType(Object.class)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        return mapper;
    }

    /*
     * Redis JSON Serializer
     */
    @Bean
    public GenericJackson2JsonRedisSerializer redisSerializer(ObjectMapper redisObjectMapper) {
        return new GenericJackson2JsonRedisSerializer(redisObjectMapper);
    }

    /*
     * Cache Configuration
     */
    @Bean
    public RedisCacheConfiguration cacheConfiguration(
            GenericJackson2JsonRedisSerializer serializer) {

        return RedisCacheConfiguration.defaultCacheConfig()

                .entryTtl(Duration.ofMinutes(10 + new Random().nextInt(5)))

                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer())
                )

                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer)
                )

                .disableCachingNullValues();
    }

    /*
     * Cache Manager
     */
    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            RedisCacheConfiguration redisCacheConfiguration) {

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }

    /*
     * RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory,
            GenericJackson2JsonRedisSerializer serializer) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();

        return template;
    }
}