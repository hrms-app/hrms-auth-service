package org.example.authenticationservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:";

    private static final String USER_PREFIX = "user:";

    private static final String REFRESH_PREFIX = "refresh:";

    private static final String ACCESS_PREFIX = "access:";


    public void addToBlacklist(String jti, long expirationMs) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + jti, true, expirationMs, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + jti));
    }

    public void cacheUser(Integer userId, Object user, long ttlMinutes) {
        redisTemplate.opsForValue().set(USER_PREFIX + userId, user, ttlMinutes, TimeUnit.MINUTES);
    }

    public Object getCachedUser(Long userId) {
        return redisTemplate.opsForValue().get(USER_PREFIX + userId);
    }

    public void removeCachedUser(Long userId) {
        redisTemplate.delete(USER_PREFIX + userId);
    }

    public void saveAccessToken(String username, String token, long expirationMs) {
        redisTemplate.opsForHash().put(ACCESS_PREFIX + username, "token", token);
        redisTemplate.opsForHash().put(ACCESS_PREFIX + username, "expiry",
                String.valueOf(System.currentTimeMillis() + expirationMs)); //convert to String
        redisTemplate.expire(ACCESS_PREFIX + username, expirationMs, TimeUnit.MILLISECONDS);
    }

    public void saveRefreshToken(String username, String token, long expirationMs) {
        redisTemplate.opsForHash().put(REFRESH_PREFIX + username, "token", token);
        redisTemplate.opsForHash().put(REFRESH_PREFIX + username, "expiry",
                String.valueOf(System.currentTimeMillis() + expirationMs)); //convert to String
        redisTemplate.expire(REFRESH_PREFIX + username, expirationMs, TimeUnit.MILLISECONDS);
    }


    public Map<Object, Object> getRefreshToken(String username) {
        return redisTemplate.opsForHash().entries(REFRESH_PREFIX + username);
    }

    public void deleteRefreshToken(String username) {
        redisTemplate.delete(REFRESH_PREFIX + username);
    }
}
