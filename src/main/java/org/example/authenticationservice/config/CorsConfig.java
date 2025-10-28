package org.example.authenticationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Cho phép gửi request từ FE
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",   // React
                "http://localhost:4200",   // Angular
                "http://localhost:5173"    // Vite / Vue
        ));

        // Cho phép gửi header Authorization để đính kèm JWT
        config.setAllowedHeaders(List.of(
                "Authorization", // là header chứa token JWT
                "Content-Type", // kiểu dữ liệu client gửi lên
                "Accept", // kiểu dữ liệu client chấp nhận
                "Origin", // nguồn gốc request
                "X-Requested-With" // header tuỳ chỉnh
        ));

        // Cho phép các method HTTP
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // Cho phép gửi cookie hoặc header `Authorization`
        config.setAllowCredentials(true);

        // Cache CORS preflight response trong 1 giờ - cho phép trình duyệt cache kết quả preflight request trả về để giảm thiểu số lần preflight request
        config.setMaxAge(3600L);

        // Áp dụng cho toàn bộ endpoint
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
