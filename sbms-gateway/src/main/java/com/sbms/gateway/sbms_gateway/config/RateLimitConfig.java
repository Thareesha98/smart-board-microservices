package com.sbms.gateway.sbms_gateway.config;




import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest()
                    .getHeaders()
                    .getFirst("X-User-Id");

            if (userId == null || userId.isEmpty()) {
                return Mono.just("anonymous");
            }

            return Mono.just(userId);
        };
    }
}