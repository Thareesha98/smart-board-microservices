package com.sbms.boarding_service.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class CacheWarmupService {

    private final BoardingCacheService cacheService;

    public CacheWarmupService(BoardingCacheService cacheService) {
        this.cacheService = cacheService;
    }

    @PostConstruct
    public void warmUpCache() {

        cacheService.getAllCached();

        System.out.println("Redis cache warmed for boardings");
    }
}