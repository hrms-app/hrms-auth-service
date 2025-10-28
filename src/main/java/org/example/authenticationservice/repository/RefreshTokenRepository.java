package org.example.authenticationservice.repository;

import org.example.authenticationservice.model.RefreshToken;
import org.example.authenticationservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);

    void deleteByUserId(Integer userId);
}
