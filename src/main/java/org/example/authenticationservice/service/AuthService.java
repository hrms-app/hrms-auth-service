package org.example.authenticationservice.service;


import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.example.authenticationservice.dto.request.LoginRequest;
import org.example.authenticationservice.dto.response.LoginResponse;
import org.example.authenticationservice.dto.response.TokenResponse;
import org.example.authenticationservice.model.RefreshToken;
import org.example.authenticationservice.model.User;
import org.example.authenticationservice.security.UserPrincipal;
import org.example.authenticationservice.security.jwt.JwtUtils;
import org.example.authenticationservice.security.jwt.TokenBlacklistService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;
    private final RedisService redisService;

    public LoginResponse login(LoginRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        String accessToken = jwtUtils.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        redisService.saveAccessToken(
                user.getUsername(),
                accessToken,
                jwtUtils.getJwtExpiration()
        );

        redisService.saveRefreshToken(
                user.getUsername(),
                refreshToken.getToken(),
                jwtUtils.getRefreshExpiration()
        );

        return new LoginResponse(accessToken, refreshToken.getToken());
    }

    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        String token = authHeader.substring(7);
        Claims claims = jwtUtils.getClaims(token);
        String jti = claims.getId();
        long exp = claims.getExpiration().getTime() - System.currentTimeMillis();

        tokenBlacklistService.addToBlacklist(jti, exp);
    }

    public TokenResponse refresh(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenStr)
                .map(refreshTokenService::verifyExpiration)
                .orElseThrow(() -> new RuntimeException("Invalid or expired refresh token"));

        String accessToken = jwtUtils.generateAccessToken(UserPrincipal.build(refreshToken.getUser()));
        return new TokenResponse(accessToken);
    }

}
