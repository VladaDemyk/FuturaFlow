package com.futura.FuturaFlow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Ось тут ми підключаємо ту саму розумну стратегію оновлення хешів
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // Тимчасово відкриваємо всі двері для Swagger і нашої реєстрації
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Вимикаємо захист, який блокує POST запити
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // Пускаємо всіх без логіну
        return http.build();
    }
}