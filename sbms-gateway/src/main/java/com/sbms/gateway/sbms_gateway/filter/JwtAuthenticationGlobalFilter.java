//package com.sbms.gateway.sbms_gateway.filter;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import reactor.core.publisher.Mono;
//
//@Component
//public class JwtAuthenticationGlobalFilter implements GlobalFilter, Ordered {
//
//    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationGlobalFilter.class);
//
//    private final ReactiveJwtDecoder jwtDecoder;
//
//    public JwtAuthenticationGlobalFilter(ReactiveJwtDecoder jwtDecoder) {
//        this.jwtDecoder = jwtDecoder;
//    }
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        String path = exchange.getRequest().getURI().getPath();
//
//        // Allow open endpoints
//        if (path.startsWith("/auth/") || path.startsWith("/api/auth/") ||
//            path.startsWith("/actuator/") || path.startsWith("/api/actuator/")) {
//
//            log.debug("Open endpoint, skipping JWT check for path: {}", path);
//            return chain.filter(exchange);
//        }
//
//        String header = exchange.getRequest().getHeaders().getFirst("Authorization");
//
//        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
//            log.info("Rejecting request to {} because Authorization header missing or invalid", path);
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//        String token = header.substring("Bearer ".length());
//
//        // Reactive decode
//        return jwtDecoder.decode(token)
//                .flatMap(jwt -> handleValidJwt(jwt, exchange, chain))
//                .onErrorResume(ex -> {
//                    log.info("JWT validation failed for path {}: {}", path, ex.getMessage());
//                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                    return exchange.getResponse().setComplete();
//                });
//    }
//
//    private Mono<Void> handleValidJwt(Jwt jwt, ServerWebExchange exchange, GatewayFilterChain chain) {
//        String email = jwt.getClaimAsString("email");
//        if (email == null) email = jwt.getSubject();
//
//        if (email != null) {
//            log.debug("JWT OK — adding X-User-Email: {}", email);
//            ServerHttpRequest mutated = exchange.getRequest()
//                    .mutate()
//                    .header("X-User-Email", email)
//                    .build();
//            return chain.filter(exchange.mutate().request(mutated).build());
//        }
//
//        log.warn("JWT valid but no email/sub claim found");
//        return chain.filter(exchange);
//    }
//
//    @Override
//    public int getOrder() {
//        return Ordered.HIGHEST_PRECEDENCE + 10;
//    }
//}








package com.sbms.gateway.sbms_gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationGlobalFilter.class);

    private final ReactiveJwtDecoder jwtDecoder;

    public JwtAuthenticationGlobalFilter(ReactiveJwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Allow open endpoints
        if (path.startsWith("/auth/") || path.startsWith("/api/auth/") ||
            path.startsWith("/actuator/") || path.startsWith("/api/actuator/") || exchange.getRequest().getMethod().matches("OPTIONS")
 ) {

            log.debug("🔓 Open endpoint — skipping JWT for {}", path);
            return chain.filter(exchange);
        }

        // Extract header
        String header = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            log.info("❌ Missing or invalid Authorization header for {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = header.substring(7);

        return jwtDecoder.decode(token)
                .flatMap(jwt -> mutateRequestWithUser(jwt, exchange, chain))
                .onErrorResume(ex -> {
                    log.info("❌ JWT validation failed: {}", ex.getMessage());
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });
    }

    private Mono<Void> mutateRequestWithUser(Jwt jwt, ServerWebExchange exchange, GatewayFilterChain chain) {

        // Extract fields from JWT
        String email = jwt.getClaimAsString("email");
        if (email == null) email = jwt.getSubject();

        Object userIdObj = jwt.getClaim("userId");
        String userId = userIdObj != null ? String.valueOf(userIdObj) : null;

        String role = jwt.getClaimAsString("role");

        log.info("✅ JWT Parsed → email={}, userId={}, role={}", email, userId, role);

        // Mutate request headers
        ServerHttpRequest mutated = exchange.getRequest()
                .mutate()
                .header("X-User-Email", email != null ? email : "")
                .header("X-User-Id", userId != null ? userId : "")
                .header("X-User-Role", role != null ? role : "")
                .build();

        return chain.filter(exchange.mutate().request(mutated).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}




