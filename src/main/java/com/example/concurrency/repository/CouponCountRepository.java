package com.example.concurrency.repository;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class CouponCountRepository {

    private RedisTemplate<String, String> redisTemplate;

    public Long increment() {
                                                    // incr couponCount
        return redisTemplate.opsForValue().increment("couponcount");
    }

    public void reset() {               // set couponcount 0
        redisTemplate.opsForValue().set("couponcount", "0");
    }
}
