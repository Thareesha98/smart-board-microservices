package com.sbms.boarding_service.service;







import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisLockService {

    private final RedissonClient redissonClient;

    public RedisLockService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public void executeWithLock(String lockKey, Runnable task) {

        RLock lock = redissonClient.getLock(lockKey);

        try {

            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {

                task.run();

            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

        } finally {

            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}