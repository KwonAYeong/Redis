package com.example.concurrency.repository;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static org.springframework.cache.interceptor.SimpleKeyGenerator.generateKey;

@Component // 클래스이므로 빈 등록
@AllArgsConstructor
public class LettuceRepository {

    private RedisTemplate<String, String> redisTemplate;

    public Boolean lock(Long key) {
        // Redis 내부에 키가 없으면 새로 만들어주는 명령어
        return redisTemplate
                .opsForValue()
                .setIfAbsent(generateKey(key), "lock", Duration.ofMillis(3_000));
    }

    public Boolean unlock(Long key) {
        return redisTemplate.delete(generateKey(key));
    }

    private String generateKey(Long key) {
        return key.toString();
    }
}
