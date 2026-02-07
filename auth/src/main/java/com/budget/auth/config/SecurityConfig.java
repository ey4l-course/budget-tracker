package com.budget.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder encoder () {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MessageDigest sha256 () {
        try {
            return MessageDigest.getInstance("SHA-256");
        }catch (NoSuchAlgorithmException e){
            //Email notify NOC
            throw new RuntimeException("Critical error: SHA-256 alg not found");
        }
    }
}
