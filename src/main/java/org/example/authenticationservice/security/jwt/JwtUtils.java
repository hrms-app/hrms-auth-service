package org.example.authenticationservice.security.jwt;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.authenticationservice.security.UserPrincipal;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Component
@NoArgsConstructor
@AllArgsConstructor
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret; // secret key để ký JWT

    @Value("${jwt.expirationMs}")
    private long jwtExpiration; // thời gian hết hạn của JWT

    @Value("${jwt.refreshExpirationMs}")
    private long refreshExpiration; // thời gian hết hạn của refresh token

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(UserPrincipal user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", user.getId())
                .claim("roles", user.getAuthorities().stream().map(a -> a.getAuthority()).toList())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    // kiểm tra tính hợp lệ của token
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // lấy username từ token
    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token)
                .getBody().getSubject();
    }

    // lấy toàn bộ claims từ token
    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}
