package com.travelhub.travelhub_api.service.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService<K,V> {

    private final RedisTemplate<K,V> redisTemplate;

    @Value("${jwt.expiration_time}")
    private long ttl;

    /**
     * TTL 은 쿠키 만료 시간과 동일하게 설정
     * opsForValue() = string 역/직렬화
     * @param key 저장할 key
     * @param value 저장할 value
     */
    public void save(K key, V value) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMillis(ttl * 3));
        log.info("[REDIS] key → {}, value → {} save success", key, value);
    }

    public void delete(K key) {
        redisTemplate.delete(key);
        log.info("[REDIS] key → {} delete success", key);
    }

    public Optional<V> get(K key) {
        V value = redisTemplate.opsForValue().get(key);
        log.info("[REDIS] key → {} get success", key);
        return Optional.ofNullable(value);
    }
}
