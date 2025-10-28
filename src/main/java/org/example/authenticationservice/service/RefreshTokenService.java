package org.example.authenticationservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.authenticationservice.exception.TokenRefreshException;
import org.example.authenticationservice.model.RefreshToken;
import org.example.authenticationservice.model.User;
import org.example.authenticationservice.repository.RefreshTokenRepository;
import org.example.authenticationservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${jwt.refreshExpirationMs}")
    private long refreshTokenDurationMs; // Thời gian hết hạn của refresh token

    /**
     * Tạo mới refresh token cho user
     * - Xóa token cũ (nếu có)
     * - Sinh token mới UUID
     * - Set thời gian hết hạn theo config
     */
    @Transactional
    public RefreshToken createRefreshToken(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // Xóa token cũ nếu có
        refreshTokenRepository.deleteByUserId(userId);

        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        return refreshTokenRepository.save(token);
    }

    /**
     *  Tìm refresh token theo chuỗi token
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     *  Kiểm tra hạn token (hết hạn thì xóa + ném exception)
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("Refresh token expired. Please login again.");
        }
        return token;
    }

    /**
     *  Xóa refresh token của user (khi logout)
     */
    public void deleteByUserId(Integer userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
