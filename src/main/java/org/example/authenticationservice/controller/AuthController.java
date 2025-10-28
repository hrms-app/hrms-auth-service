package org.example.authenticationservice.controller;


import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.example.authenticationservice.dto.request.LoginRequest;
import org.example.authenticationservice.dto.request.TokenRefreshRequest;
import org.example.authenticationservice.dto.response.LoginResponse;
import org.example.authenticationservice.dto.response.TokenResponse;
import org.example.authenticationservice.security.jwt.JwtUtils;
import org.example.authenticationservice.security.jwt.TokenBlacklistService;
import org.example.authenticationservice.service.AuthService;
import org.example.authenticationservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            authService.logout(authHeader);
            return ResponseEntity.ok("User logged out successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
