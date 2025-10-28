package org.example.authenticationservice.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:";

    /**
     * Thêm token (hoặc jti) vào danh sách blacklist.
     *
     * @param tokenId     ID duy nhất của token (thường là jti)
     * @param expirationMs thời gian sống còn lại của token (tính bằng ms)
     */
    public void addToBlacklist(String tokenId, long expirationMs) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + tokenId, "true", expirationMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Kiểm tra token có nằm trong blacklist không.
     *
     * @param tokenId ID của token
     * @return true nếu token bị thu hồi
     */
    public boolean isBlacklisted(String tokenId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + tokenId));
    }

    /**
     * Xóa token khỏi blacklist thủ công (ít khi dùng).
     */
    public void removeFromBlacklist(String tokenId) {
        redisTemplate.delete(BLACKLIST_PREFIX + tokenId);
    }
}
