package com.sbms.boarding_service.config;

import com.sbms.boarding_service.service.RedisRateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RedisRateLimiterService rateLimiter;

    public RateLimitInterceptor(RedisRateLimiterService rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        String ip = request.getRemoteAddr();

        String key = "rate_limit:" + ip;

        boolean allowed = rateLimiter.isAllowed(key, 20, 10);

        if (!allowed) {

            response.setStatus(429);
            response.getWriter().write("Too many requests");

            return false;
        }

        return true;
    }
}