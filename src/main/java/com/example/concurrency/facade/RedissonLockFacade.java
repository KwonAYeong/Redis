package com.example.concurrency.facade;

import com.example.concurrency.service.InventoryService;
import lombok.AllArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class RedissonLockFacade {

    // Redisson 은 아래 필드가 락 설정 및 해제를 담당한다.
    private RedissonClient redissonClient;

    private InventoryService inventoryService;

    public void decrease(Long key, Long count) {
        RLock lock = redissonClient.getLock(key.toString());

        try {
            boolean avail = lock.tryLock(10, 1, TimeUnit.SECONDS);

            if(!avail) {
                System.out.println("lock 획득 실패");
                return;
            }
            inventoryService.decrease(key, count);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        } finally {
            lock.unlock();
        }
    }
}
