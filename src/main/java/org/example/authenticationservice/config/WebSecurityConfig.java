package org.example.authenticationservice.config;

import org.example.authenticationservice.security.jwt.AuthEntryPointJwt;
import lombok.RequiredArgsConstructor;
import org.example.authenticationservice.security.jwt.AuthTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final AuthTokenFilter authTokenFilter;
    private final AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable()) // tắt session
                .exceptionHandling(e -> e.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // không lưu session
                .authorizeHttpRequests(auth -> auth // phan quyền truy cập
                        .requestMatchers("/api/auth/**").permitAll() // cho phép truy cập không cần xác thực
                        .anyRequest().authenticated()) // các yêu cầu khác cần xác thực
                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class) // thêm bộ lọc xác thực JWT
                .build();
    }

    // Cấu hình AuthenticationManager để sử dụng trong các service
    // authenticationManager : kiểm tra user và mật khẩu có hợp lệ không -> sinh authentication
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
