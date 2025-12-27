package com.sbms.gateway.sbms_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.warn("🔥 Request passed through gateway: {} {}", 
                 exchange.getRequest().getMethod(),
                 exchange.getRequest().getURI());

        return chain.filter(exchange)
            .doOnSuccess(a -> log.warn("✅ Response from backend"));
    }

    @Override
    public int getOrder() {
        return -1; // make sure filter runs first
    }
}

